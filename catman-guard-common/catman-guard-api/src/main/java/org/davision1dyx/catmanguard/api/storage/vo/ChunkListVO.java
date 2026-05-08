package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-07
 * @description
 */
@Data
public class ChunkListVO {

    private List<ChunkItemVO> chunks;
    private Integer maxChunkSize;
    private Integer minChunkSize;
    private Integer avgChunkSize;

    public static ChunkListVO build(List<ChunkItemVO> chunks, Integer maxChunkSize,
                                   Integer minChunkSize, Integer avgChunkSize) {
        ChunkListVO chunkListVO = new ChunkListVO();
        chunkListVO.setChunks(chunks);
        chunkListVO.setMaxChunkSize(maxChunkSize);
        chunkListVO.setMinChunkSize(minChunkSize);
        chunkListVO.setAvgChunkSize(avgChunkSize);
        return chunkListVO;
    }

    @Data
    public static class ChunkItemVO {
        private String chunkId;
        private String content;
        private Integer chunkIndex;
        private Long size;

        public static ChunkItemVO build(String chunkId, String content, Integer chunkIndex, Long size) {
            ChunkItemVO item = new ChunkItemVO();
            item.setChunkId(chunkId);
            item.setContent(content);
            item.setChunkIndex(chunkIndex);
            item.setSize(size);
            return item;
        }
    }
}