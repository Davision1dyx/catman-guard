package org.davision1dyx.catmanguard.api.storage.service;

import org.davision1dyx.catmanguard.api.storage.dto.FileUploadDTO;
import org.davision1dyx.catmanguard.api.storage.vo.FileUploadVO;

/**
 * @author Davison
 * @date 2026-05-01
 * @description
 */
public interface FileService {
    FileUploadVO upload(FileUploadDTO fileUploadDTO);
}
