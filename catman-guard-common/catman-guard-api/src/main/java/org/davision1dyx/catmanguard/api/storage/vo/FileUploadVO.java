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
    private String status;
    private Long size;

    public static FileUploadVO build(String fileId, String fileUrl, String status, Long size) {
        FileUploadVO fileUploadVO = new FileUploadVO();
        fileUploadVO.setFileId(fileId);
        fileUploadVO.setFileUrl(fileUrl);
        fileUploadVO.setStatus(status);
        fileUploadVO.setSize(size);
        return fileUploadVO;
    }
}
