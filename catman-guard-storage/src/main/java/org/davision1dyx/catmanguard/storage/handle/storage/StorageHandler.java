package org.davision1dyx.catmanguard.storage.handle.storage;

import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.file.properties.FileProperties;
import org.davision1dyx.catmanguard.storage.handle.storage.strategy.StorageStrategy;
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
    private final FileProperties fileProperties;

    public StorageHandler(List<StorageStrategy> storageStrategies, FileProperties fileProperties) {
        this.storageStrategies = storageStrategies;
        this.fileProperties = fileProperties;
    }

    public String handle(MultipartFile file) {
        Optional<String> uploadUrl = storageStrategies.stream().filter(strategy -> strategy.support(fileProperties.getMode()))
                .map(strategy -> strategy.upload(file)).findFirst();
        return uploadUrl.orElseThrow(() -> new BizException(ErrorCode.NO_FILE_TYPE_SUPPORT));
    }
}
