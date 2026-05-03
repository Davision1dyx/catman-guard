package org.davision1dyx.catmanguard.storage.handle.storage;

import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.davision1dyx.catmanguard.storage.handle.storage.strategy.StorageStrategy;
import org.davision1dyx.catmanguard.storage.pojo.StorageHandleInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * @author Davison
 * @date 2026-05-01
 * @description 存储策略器
 */
@Component
public class StorageHandler {

    private final List<StorageStrategy> storageStrategies;

    public StorageHandler(List<StorageStrategy> storageStrategies) {
        this.storageStrategies = storageStrategies;
    }

    public StorageHandleInfo handleUpload(MultipartFile file, FileMode fileMode) {
        Optional<StorageHandleInfo> upload = storageStrategies.stream().filter(strategy -> strategy.support(fileMode))
                .map(strategy -> strategy.upload(file)).findFirst();
        return upload.orElseThrow(() -> new BizException(ErrorCode.NO_FILE_TYPE_SUPPORT));
    }

    public StorageHandleInfo handleUpload(byte[] bytes, String fileName, String contentType, FileMode fileMode) {
        Optional<StorageHandleInfo> upload = storageStrategies.stream().filter(strategy -> strategy.support(fileMode))
                .map(strategy -> strategy.upload(bytes, fileName, contentType)).findFirst();
        return upload.orElseThrow(() -> new BizException(ErrorCode.NO_FILE_TYPE_SUPPORT));
    }

    public byte[] handleDownload(String fileUrl, FileMode fileMode) {
        Optional<byte[]> download = storageStrategies.stream().filter(strategy -> strategy.support(fileMode))
                .map(strategy -> strategy.download(fileUrl)).findFirst();
        return download.orElseThrow(() -> new BizException(ErrorCode.NO_FILE_TYPE_SUPPORT));
    }
}
