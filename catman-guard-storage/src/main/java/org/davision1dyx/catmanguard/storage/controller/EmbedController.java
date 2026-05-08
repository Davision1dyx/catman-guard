package org.davision1dyx.catmanguard.storage.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.EmbedSyncDTO;
import org.davision1dyx.catmanguard.api.storage.service.ChunkService;
import org.davision1dyx.catmanguard.api.storage.vo.EmbedSyncVO;
import org.springframework.web.bind.annotation.*;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 向量库管理接口
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/storage/embed")
public class EmbedController {

    private final ChunkService chunkService;

    public EmbedController(ChunkService chunkService) {
        this.chunkService = chunkService;
    }

    /**
     * 同步向量库
     * @param knowledgeId 知识库ID（当前未使用，预留）
     * @return 同步结果
     */
    @PostMapping("/sync/{knowledgeId}")
    public EmbedSyncVO sync(@PathVariable String knowledgeId) {
        log.info("[POST] /processing/catman/storage/embed/sync/{}", knowledgeId);

        EmbedSyncDTO dto = EmbedSyncDTO.build(knowledgeId);
        return chunkService.sync(dto);
    }
}