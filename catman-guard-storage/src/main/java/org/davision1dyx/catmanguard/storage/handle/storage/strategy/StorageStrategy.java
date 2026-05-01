package org.davision1dyx.catmanguard.storage.handle.storage.strategy;

import org.davision1dyx.catmanguard.base.constant.CommonConstant;
import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Davison
 * @date 2026-05-01
 * @description
 */
public interface StorageStrategy {

    boolean support(FileMode fileMode);

    String upload(MultipartFile file);

    default String getFileType(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return CommonConstant.UNKNOWN;
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < fileName.length() - 1) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return CommonConstant.UNKNOWN;
    }
}
