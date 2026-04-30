package org.davision1dyx.catmanguard.file.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.davision1dyx.catmanguard.file.properties.FileProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Configuration
@EnableConfigurationProperties(value = FileProperties.class)
public class FileConfiguration {

    @Bean
    @ConditionalOnExpression("'${catname.file.mode}' == 'minio'")
    public MinioClient minioClient(FileProperties fileProperties) throws Exception {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(fileProperties.getMinio().getEndpoint())
                .credentials(fileProperties.getMinio().getAccessKey(), fileProperties.getMinio().getSecretKey())
                .build();
        createBucketIfNotExists(minioClient, fileProperties.getMinio().getBucketName(), true);
        return minioClient;
    }

    private void createBucketIfNotExists(MinioClient minioClient, String minioBucketName, boolean publicRead) throws Exception {
        if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(minioBucketName).build())) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(minioBucketName).build());

            // 设置 bucket 策略为公共读
            if (publicRead) {
                String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + minioBucketName + "/*\"]}]}";
                minioClient.setBucketPolicy(
                        SetBucketPolicyArgs.builder()
                                .bucket(minioBucketName)
                                .config(policy)
                                .build()
                );
            }
        }
    }
}
