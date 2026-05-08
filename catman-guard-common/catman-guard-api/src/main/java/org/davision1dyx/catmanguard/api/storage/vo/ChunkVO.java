package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-07
 * @description
 */
@Data
public class ChunkVO {

    private String fileId;
    private String status;
    private List<ChunkItemVO> chunks;

    public static ChunkVO build(String fileId, String status, List<ChunkItemVO> chunks) {
        ChunkVO chunkVO = new ChunkVO();
        chunkVO.setFileId(fileId);
        chunkVO.setStatus(status);
        chunkVO.setChunks(chunks);
        return chunkVO;
    }

    @Data
    public static class ChunkItemVO {
        private String chunkId;
        private String content;
        private Long size;

        public static ChunkItemVO build(String chunkId, String content, Long size) {
            ChunkItemVO item = new ChunkItemVO();
            item.setChunkId(chunkId);
            item.setContent(content);
            item.setSize(size);
            return item;
        }
    }
}