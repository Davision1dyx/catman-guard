package org.davision1dyx.catmanguard.storage.handle.storage.strategy;

import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.davision1dyx.catmanguard.storage.pojo.StorageHandleInfo;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Davison
 * @date 2026-05-01
 * @description
 */
public interface StorageStrategy {

    boolean support(FileMode fileMode);

    StorageHandleInfo upload(MultipartFile file);

    StorageHandleInfo upload(byte[] bytes, String fileName, String contentType);

    byte[] download(String fileUrl);
}
