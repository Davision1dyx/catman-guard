package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Davison
 * @date 2026-05-01
 * @description
 */
@Data
public class FileUploadDTO {

    private MultipartFile file;
    private String version;
    private String tag;

    public static FileUploadDTO build(MultipartFile file, String version, String tag) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFile(file);
        fileUploadDTO.setVersion(version);
        fileUploadDTO.setTag(tag);
        return fileUploadDTO;
    }
}