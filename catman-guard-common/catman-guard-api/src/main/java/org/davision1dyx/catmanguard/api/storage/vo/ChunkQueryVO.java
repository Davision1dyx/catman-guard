package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-05
 * @description
 */
@Data
public class ChunkQueryVO {
    private String chunkId;
    private String fileId;
    private Integer chunkIndex;
    private String content;
    private String metaData;
}
