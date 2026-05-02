package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-01
 * @description
 */
@Data
public class FileUploadVO {
    private String fileId;
    private String fileUrl;

    public static FileUploadVO build(String fileId, String fileUrl, String status) {
        FileUploadVO fileUploadVO = new FileUploadVO();
        fileUploadVO.setFileId(fileId);
        fileUploadVO.setFileUrl(fileUrl);
        return fileUploadVO;
    }
}
