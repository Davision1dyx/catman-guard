package org.davision1dyx.catmanguard.storage.handle.storage.strategy;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.base.constant.CommonConstant;
import org.davision1dyx.catmanguard.base.util.FileUtil;
import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.davision1dyx.catmanguard.file.properties.FileProperties;
import org.davision1dyx.catmanguard.storage.pojo.StorageHandleInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * @author Davison
 * @date 2026-05-01
 * @description 本地存储策略
 */
@Slf4j
@Component
public class LocalStorageStrategy implements StorageStrategy {

    private final FileProperties fileProperties;

    public LocalStorageStrategy(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    @Override
    public boolean support(FileMode fileMode) {
        return FileMode.Local.equals(fileMode);
    }

    @Override
    public StorageHandleInfo upload(MultipartFile file) {
        try {
            String fileType = FileUtil.getFileType(file.getOriginalFilename());
            String basePath = fileProperties.getLocal().getPath();
            String fileName = UUID.randomUUID().toString() + CommonConstant.DOT + fileType;
            String filePath = basePath + CommonConstant.FILE_SEPARATOR + fileName;
            file.transferTo(new File(filePath));
            return new StorageHandleInfo(fileName, filePath);
        } catch (Exception e) {
            log.error("文件上传失败, 文件名：{}", file.getName(), e);
            return null;
        }
    }

    @Override
    public StorageHandleInfo upload(byte[] bytes, String fileName, String contentType) {
        try {
            String basePath = fileProperties.getLocal().getPath();
            String filePath = basePath + CommonConstant.FILE_SEPARATOR + fileName;
            FileUtil.writeBytes(bytes, filePath);
            return new StorageHandleInfo(fileName, filePath);
        } catch (Exception e) {
            log.error("文件上传失败, 文件名：{}", fileName, e);
            return null;
        }
    }

    @Override
    public byte[] download(String fileUrl) {
        try {
            return FileUtil.readFileToBytes(Paths.get(fileUrl));
        } catch (Exception e) {
            log.error("文件下载失败, 文件名：{}", fileUrl, e);
            return null;
        }
    }
}
