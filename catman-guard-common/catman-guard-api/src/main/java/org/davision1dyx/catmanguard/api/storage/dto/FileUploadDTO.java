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
    private String fileTitle;
    private String description;

    public static FileUploadDTO build(MultipartFile file, String fileTitle, String description) {
        FileUploadDTO fileUploadDTO = new FileUploadDTO();
        fileUploadDTO.setFile(file);
        fileUploadDTO.setFileTitle(fileTitle);
        fileUploadDTO.setDescription(description);
        return fileUploadDTO;
    }
}