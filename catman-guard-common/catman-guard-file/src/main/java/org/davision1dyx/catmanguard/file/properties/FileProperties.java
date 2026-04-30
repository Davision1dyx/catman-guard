package org.davision1dyx.catmanguard.file.properties;

import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@ConfigurationProperties(prefix = "catman.file")
public class FileProperties {

    private LocalProperties local;
    private MinioProperties minio;
    private FileMode mode;

    public static class LocalProperties {
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }

    public static class MinioProperties {
        private String endpoint;
        private String bucketName;
        private String accessKey;
        private String secretKey;

        public String getEndpoint() {
            return endpoint;
        }

        public void setEndpoint(String endpoint) {
            this.endpoint = endpoint;
        }

        public String getBucketName() {
            return bucketName;
        }

        public void setBucketName(String bucketName) {
            this.bucketName = bucketName;
        }

        public String getAccessKey() {
            return accessKey;
        }

        public void setAccessKey(String accessKey) {
            this.accessKey = accessKey;
        }

        public String getSecretKey() {
            return secretKey;
        }

        public void setSecretKey(String secretKey) {
            this.secretKey = secretKey;
        }
    }

    public LocalProperties getLocal() {
        return local;
    }

    public void setLocal(LocalProperties local) {
        this.local = local;
    }

    public MinioProperties getMinio() {
        return minio;
    }

    public void setMinio(MinioProperties minio) {
        this.minio = minio;
    }

    public FileMode getMode() {
        return mode;
    }

    public void setMode(FileMode mode) {
        this.mode = mode;
    }
}
