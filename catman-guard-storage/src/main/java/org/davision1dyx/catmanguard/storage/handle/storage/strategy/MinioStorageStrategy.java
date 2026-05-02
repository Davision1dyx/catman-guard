package org.davision1dyx.catmanguard.storage.handle.storage.strategy;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.base.constant.CommonConstant;
import org.davision1dyx.catmanguard.base.util.FileUtil;
import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.davision1dyx.catmanguard.file.properties.FileProperties;
import org.davision1dyx.catmanguard.storage.pojo.StorageHandleInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.UUID;

/**
 * @author Davison
 * @date 2026-05-01
 * @description minio存储策略
 */
@Slf4j
@Component
public class MinioStorageStrategy implements StorageStrategy {

    private final FileProperties fileProperties;
    private final MinioClient minioClient;

    public MinioStorageStrategy(FileProperties fileProperties, MinioClient minioClient) {
        this.fileProperties = fileProperties;
        this.minioClient = minioClient;
    }

    @Override
    public boolean support(FileMode fileMode) {
        return FileMode.Minio.equals(fileMode);
    }

    @Override
    public StorageHandleInfo upload(MultipartFile file) {
        try {
            String fileType = FileUtil.getFileType(file.getOriginalFilename());
            String objectName = UUID.randomUUID() + "." + fileType;
            String endpoint = fileProperties.getMinio().getEndpoint();
            String bucketName = fileProperties.getMinio().getBucketName();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return new StorageHandleInfo(objectName,
                    endpoint + CommonConstant.FILE_SEPARATOR + bucketName + CommonConstant.FILE_SEPARATOR + objectName);
        } catch (Exception e) {
            log.error("文件上传失败, 文件名：{}", file.getName(), e);
            return null;
        }
    }

    @Override
    public StorageHandleInfo upload(byte[] bytes, String fileName, String contentType) {
        try {
            String endpoint = fileProperties.getMinio().getEndpoint();
            String bucketName = fileProperties.getMinio().getBucketName();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(new ByteArrayInputStream(bytes), bytes.length, -1)
                            .contentType(contentType)
                            .build()
            );
            return new StorageHandleInfo(fileName,
                    endpoint + CommonConstant.FILE_SEPARATOR + bucketName + CommonConstant.FILE_SEPARATOR + fileName);
        } catch (Exception e) {
            log.error("文件上传失败, 文件名：{}", fileName, e);
            return null;
        }
    }
}
