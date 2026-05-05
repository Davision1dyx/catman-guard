package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-05
 * @description
 */
@Data
public class ChunkQueryDTO {
    private String fileId;
    private Integer chunkIndex;
}
