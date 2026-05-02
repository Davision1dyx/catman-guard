package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@Data
public class FileRecognizeDTO {

    private String fileId;
    private String fileName;
    private String fileTitle;
    private MultipartFile file;
    private Boolean returnImage;
}
