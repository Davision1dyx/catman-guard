package org.davision1dyx.catmanguard.storage.handle.transform;

import org.davision1dyx.catmanguard.storage.enums.ChunkType;
import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-30
 * @description 分片处理器
 */
public class DocumentChunkHandler {

    /**
     * 每个分片的最大字符数，0表示不限制
     */
    private final int chunkSize;

    /**
     * 相邻分片之间的重叠字符数
     */
    private final int overlap;

    public DocumentChunkHandler(int chunkSize, int overlap) {
        this.chunkSize = chunkSize;
        this.overlap = overlap;
    }

    public List<Document> handle(List<Document> documents, ChunkType chunkType) {
        switch (chunkType) {
            case FIXED_LENGTH:
                return new OverlapParagraphTextSplitHandler(chunkSize, overlap).split(documents);
            case PARENT_CHILD:
                return new MarkdownHeaderBrotherTextSplitHandler(chunkSize, overlap).split(documents);
            case SEMANTIC:
                return new OverlapParagraphTextSplitHandler(chunkSize, overlap).split(documents);
            default:
                return new OverlapParagraphTextSplitHandler(chunkSize, overlap).split(documents);
        }
    }
}
