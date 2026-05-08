package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Davison
 * @date 2026-05-07
 * @description
 */
@Data
public class FileListVO {

    private Integer code;
    private List<FileItemVO> data;

    public static FileListVO build(List<FileItemVO> data) {
        FileListVO fileListVO = new FileListVO();
        fileListVO.setCode(200);
        fileListVO.setData(data);
        return fileListVO;
    }

    @Data
    public static class FileItemVO {
        private String fileId;
        private String fileTitle;
        private String fileName;
        private String version;
        private String extension;
        private String description;
        private String microservice;
        private String status;
        private Long size;
        private LocalDateTime createTime;
        private LocalDateTime updateTime;

        public static FileItemVO build(String fileId, String fileTitle, String fileName, String version,
                                       String extension, String description, String microservice,
                                       String status, Long size, java.time.LocalDateTime createTime,
                                       java.time.LocalDateTime updateTime) {
            FileItemVO item = new FileItemVO();
            item.setFileId(fileId);
            item.setFileTitle(fileTitle);
            item.setFileName(fileName);
            item.setVersion(version);
            item.setExtension(extension);
            item.setDescription(description);
            item.setMicroservice(microservice);
            item.setStatus(status);
            item.setSize(size);
            item.setCreateTime(createTime);
            item.setUpdateTime(updateTime);
            return item;
        }
    }
}