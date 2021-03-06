package org.incredible.certProcessor;

import org.incredible.builders.*;
import org.incredible.certProcessor.signature.SignatureHelper;
import org.incredible.pojos.CertificateExtension;
import org.incredible.pojos.ob.Criteria;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import org.incredible.pojos.ob.SignedVerification;
import org.incredible.pojos.ob.exeptions.InvalidDateFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CertificateFactory {

    private static String uuid;

    private static Logger logger = LoggerFactory.getLogger(CertificateFactory.class);

    final String resourceName = "application.properties";

    private static SignatureHelper signatureHelper;

    public CertificateExtension createCertificate(CertModel certModel, Map<String, String> properties) throws InvalidDateFormatException {

        uuid = jsonKey.DOMAIN_PATH + UUID.randomUUID().toString();

        CertificateExtensionBuilder certificateExtensionBuilder = new CertificateExtensionBuilder(jsonKey.CONTEXT);
        CompositeIdentityObjectBuilder compositeIdentityObjectBuilder = new CompositeIdentityObjectBuilder(jsonKey.CONTEXT);
        BadgeClassBuilder badgeClassBuilder = new BadgeClassBuilder(jsonKey.CONTEXT);
        IssuerBuilder issuerBuilder = new IssuerBuilder(jsonKey.CONTEXT);
        SignedVerification signedVerification = new SignedVerification();


        Criteria criteria = new Criteria();
        criteria.setNarrative("For exhibiting outstanding performance");
        criteria.setId(uuid);


        //todo decide hosted or signed badge based on config
        if ((jsonKey.VERIFICATION_TYPE).equals("hosted")) {
            signedVerification.setType(new String[]{jsonKey.VERIFICATION_TYPE});
        } else {
            signedVerification.setCreator(jsonKey.PUBLIC_KEY_URL);
        }

        /**
         *  recipient object
         *  **/
        compositeIdentityObjectBuilder.setName(certModel.getRecipientName()).setId(certModel.getIdentifier())
                .setHashed(false).
                setType(new String[]{"id"});


        issuerBuilder.setId(jsonKey.ISSUER_URL).setName(certModel.getIssuer().getName());
        /**
         * badge class object
         * **/

        badgeClassBuilder.setName(certModel.getCourseName()).setDescription(certModel.getCertificateDescription())
                .setId(jsonKey.BADGE_URL).setCriteria(criteria)
                .setImage(certModel.getCertificateLogo()).
                setIssuer(issuerBuilder.build());


        /**
         *
         * Certificate extension object
         */
        certificateExtensionBuilder.setId(uuid).setRecipient(compositeIdentityObjectBuilder.build())
                .setBadge(badgeClassBuilder.build())
                .setIssuedOn(certModel.getIssuedDate()).setExpires(certModel.getExpiry())
                .setValidFrom(certModel.getValidFrom()).setVerification(signedVerification).setSignatory(certModel.getSignatoryList());


//        /**
//         * to assign signature value
//         */
//        initSignatureHelper(certModel.getSignatoryList());
//        /** certificate before signature value **/
//        String toSignCertificate = certificateExtensionBuilder.build().toString();
//
//        String signatureValue = getSignatureValue(toSignCertificate);
//
//        signatureBuilder.setCreated(Instant.now().toString()).setCreator("https://dgt.example.gov.in/keys/awarding_body.json")
//                .setSignatureValue(signatureValue);
//        certificateExtensionBuilder.setSignature(signatureBuilder.build());
//
//        logger.info("signed certificate is valid {}", verifySignature(toSignCertificate, signatureValue));
        logger.info("certificate extension => {}", certificateExtensionBuilder.build());
        return certificateExtensionBuilder.build();
    }


    public Properties readPropertiesFile() {
        ClassLoader loader = CertificateFactory.class.getClassLoader();
        Properties properties = new Properties();
        try (InputStream resourceStream = loader.getResourceAsStream(resourceName)) {
            properties.load(resourceStream);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Exception while reading application.properties {}", e.getMessage());
        }
        return properties;
    }

    public static boolean verifySignature(String certificate, String signatureValue) {
        boolean isValid = false;
        try {
            isValid = signatureHelper.verify(certificate.getBytes(),
                    signatureValue);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        }
        return isValid;
    }

    private static void initSignatureHelper(KeyPair keyPair) {

        try {
            signatureHelper = new SignatureHelper("SHA1withRSA", keyPair);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }


    private static String getSignatureValue(String toSignCertificate) {
        try {
            String signatureValue = signatureHelper.sign(toSignCertificate.getBytes());
            return signatureValue;
        } catch (SignatureException | UnsupportedEncodingException | InvalidKeyException e) {
            e.printStackTrace();
            return null;
        }

    }
}

