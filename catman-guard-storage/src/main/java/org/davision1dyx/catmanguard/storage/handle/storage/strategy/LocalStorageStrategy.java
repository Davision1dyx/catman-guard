package org.davision1dyx.catmanguard.storage.handle.storage.strategy;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.base.constant.CommonConstant;
import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.davision1dyx.catmanguard.file.properties.FileProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
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
    public String upload(MultipartFile file) {
        try {
            String fileType = getFileType(file.getOriginalFilename());
            String basePath = fileProperties.getLocal().getPath();
            String name = UUID.randomUUID().toString();
            String filePath = basePath + CommonConstant.FILE_SEPARATOR + name + "." + fileType;
            file.transferTo(new File(filePath));
            return filePath;
        } catch (Exception e) {
            log.error("文件上传失败, 文件名：{}", file.getName(), e);
            return null;
        }
    }
}
