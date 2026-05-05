package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Davison
 * @date 2026-05-05
 * @description
 */
@Data
public class ChunkEmbedVO {
    private Boolean success;
    private Integer chunkSize;

    public static ChunkEmbedVO fail() {
        ChunkEmbedVO vo = new ChunkEmbedVO();
        vo.setSuccess(false);
        return vo;
    }

    public static ChunkEmbedVO success(Integer chunkSize) {
        ChunkEmbedVO vo = new ChunkEmbedVO();
        vo.setSuccess(true);
        vo.setChunkSize(chunkSize);
        return vo;
    }
}
