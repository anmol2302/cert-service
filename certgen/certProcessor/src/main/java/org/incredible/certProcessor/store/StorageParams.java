package org.incredible.certProcessor.store;

import org.apache.commons.lang3.StringUtils;
import org.sunbird.cloud.storage.BaseStorageService;
import org.sunbird.cloud.storage.factory.StorageConfig;
import java.io.File;
import java.util.Map;

import static org.sunbird.cloud.storage.factory.StorageServiceFactory.getStorageService;

public class StorageParams {


    private static BaseStorageService storageService = null;

    private static Map<String, String> properties;

    public StorageParams(Map<String, String> properties) {
        this.properties = properties;
    }

    public void init() {
        String cloudStoreType = properties.get("CLOUD_STORAGE_TYPE");
        if (StringUtils.equalsIgnoreCase(cloudStoreType, "azure")) {
            String storageKey = properties.get("AZURE_STORAGE_KEY");
            String storageSecret = properties.get("AZURE_STORAGE_SECRET");
            StorageConfig storageConfig = new StorageConfig(cloudStoreType, storageKey, storageSecret);
            storageService = getStorageService(storageConfig);
            } else if (StringUtils.equalsIgnoreCase(cloudStoreType, "aws")) {
                String storageKey = properties.get("AWS_STORAGE_KEY");
                String storageSecret = properties.get("AWS_STORAGE_SECRET");
                storageService = getStorageService(new StorageConfig(cloudStoreType, storageKey, storageSecret));
        } else {
//            throw new ServerException("ERR_INVALID_CLOUD_STORAGE Error while initialising cloud storage");
        }
    }

    public String upload(String container, String path, File file, boolean isDirectory) {
        CloudStorage cloudStorage = new CloudStorage(storageService);
        return cloudStorage.uploadFile(container, path, file, isDirectory);
    }
}
