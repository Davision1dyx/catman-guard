package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-07
 * @description
 */
@Data
public class ChunkListDTO {

    private String fileId;

    public static ChunkListDTO build(String fileId) {
        ChunkListDTO chunkListDTO = new ChunkListDTO();
        chunkListDTO.setFileId(fileId);
        return chunkListDTO;
    }
}