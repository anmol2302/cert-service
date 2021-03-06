package controllers.certs;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.sunbird.BaseException;
import org.sunbird.message.IResponseMessage;
import org.sunbird.message.ResponseCode;
import org.sunbird.request.Request;
import org.sunbird.JsonKey;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class contains method to validate certificate api request
 */
public class CertValidator {

    /**
     * This method will validate generate certificate request
     * @param request
     * @throws BaseException
     */
    public static void validateGenerateCertRequest(Request request) throws BaseException {
        Map<String,Object> certReq = (Map<String,Object>)request.getRequest().get(JsonKey.CERTIFICATE);
        checkMandatoryParamsPresent(certReq,JsonKey.CERTIFICATE,Arrays.asList(JsonKey.COURSE_NAME,JsonKey.NAME,JsonKey.HTML_TEMPLATE,JsonKey.TAG));
        validateCertData((List<Map<String, Object>>)certReq.get(JsonKey.DATA));
        validateCertIssuer((Map<String, Object>)certReq.get(JsonKey.ISSUER));
        validateCertSignatoryList((List<Map<String, Object>>)certReq.get(JsonKey.SIGNATORY_LIST));
    }

    private static void validateCertSignatoryList(List<Map<String, Object>> signatoryList) throws BaseException {
        checkMandatoryParamsPresent(signatoryList,JsonKey.CERTIFICATE+"."+JsonKey.SIGNATORY_LIST,Arrays.asList(JsonKey.NAME,JsonKey.ID,JsonKey.DESIGNATION));
    }

    private static void validateCertIssuer(Map<String, Object> issuer) throws BaseException {
        checkMandatoryParamsPresent(issuer,JsonKey.CERTIFICATE+"."+JsonKey.ISSUER,Arrays.asList(JsonKey.NAME,JsonKey.URL));
    }

    private static void validateCertData(List<Map<String, Object>> data) throws BaseException {
        checkMandatoryParamsPresent(data,JsonKey.CERTIFICATE+"."+JsonKey.DATA,Arrays.asList(JsonKey.RECIPIENT_NAME));
    }

    private static void checkMandatoryParamsPresent(
            List<Map<String, Object>> data,String parentKey, List<String> keys) throws BaseException {
        if (CollectionUtils.isEmpty(data)) {
            throw new BaseException("MANDATORY_PARAMETER_MISSING",
                    MessageFormat.format(IResponseMessage.MANDATORY_PARAMETER_MISSING,parentKey),
                    ResponseCode.CLIENT_ERROR.getCode());
        }
        for(Map<String, Object> map:data){
            checkChildrenMapMandatoryParams(map,keys,parentKey);
        }

    }

    private static void checkMandatoryParamsPresent(
            Map<String, Object> data,String parentKey, List<String> keys) throws BaseException {
        if (MapUtils.isEmpty(data)) {
            throw new BaseException("MANDATORY_PARAMETER_MISSING",
                    MessageFormat.format(IResponseMessage.MANDATORY_PARAMETER_MISSING,parentKey),
                    ResponseCode.CLIENT_ERROR.getCode());
        }
        checkChildrenMapMandatoryParams(data,keys,parentKey);
    }

    private static void checkChildrenMapMandatoryParams(Map<String, Object> data,List<String> keys, String parentKey) throws BaseException {

        for (String key : keys) {
            if (StringUtils.isEmpty((String) data.get(key))) {
                throw new BaseException("MANDATORY_PARAMETER_MISSING",
                        MessageFormat.format(IResponseMessage.MANDATORY_PARAMETER_MISSING, parentKey+"."+key),
                        ResponseCode.CLIENT_ERROR.getCode());
            }
        }
    }

}

