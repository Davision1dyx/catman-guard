package org.davision1dyx.catmanguard.storage.service;

import org.davision1dyx.catmanguard.api.storage.dto.FileUploadDTO;
import org.davision1dyx.catmanguard.api.storage.service.FileService;
import org.davision1dyx.catmanguard.api.storage.vo.FileUploadVO;
import org.davision1dyx.catmanguard.file.properties.FileProperties;

/**
 * @author Davison
 * @date 2026-05-01
 * @description
 */
public class FileServiceImpl implements FileService {

    private final FileProperties fileProperties;

    public FileServiceImpl(FileProperties fileProperties) {
        this.fileProperties = fileProperties;
    }

    @Override
    public FileUploadVO upload(FileUploadDTO fileUploadDTO) {
        return null;
    }
}
