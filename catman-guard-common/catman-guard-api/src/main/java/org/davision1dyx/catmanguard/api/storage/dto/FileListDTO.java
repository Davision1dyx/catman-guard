package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-07
 * @description
 */
@Data
public class FileListDTO {

    private String knowledgeId;
    private String search;
    private String fileType;
    private String status;

    public static FileListDTO build(String knowledgeId, String search, String fileType, String status) {
        FileListDTO fileListDTO = new FileListDTO();
        fileListDTO.setKnowledgeId(knowledgeId);
        fileListDTO.setSearch(search);
        fileListDTO.setFileType(fileType);
        fileListDTO.setStatus(status);
        return fileListDTO;
    }
}