package org.davision1dyx.catmanguard.storage.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkListDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileSplitDTO;
import org.davision1dyx.catmanguard.api.storage.service.ChunkService;
import org.davision1dyx.catmanguard.api.storage.service.FileService;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkListVO;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkVO;
import org.springframework.web.bind.annotation.*;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 分片管理接口
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/storage/chunk")
public class ChunkController {

    private final ChunkService chunkService;
    private final FileService fileService;

    public ChunkController(ChunkService chunkService, FileService fileService) {
        this.chunkService = chunkService;
        this.fileService = fileService;
    }

    /**
     * 执行分片
     * @param fileId 文件ID
     * @param requestBody 分片参数
     * @return
     */
    @PostMapping("/chunk/{fileId}")
    public ChunkVO chunk(@PathVariable String fileId, @RequestBody FileSplitDTO requestBody) {
        log.info("[POST] /processing/catman/storage/chunk/chunk/{}", fileId);

        if (requestBody.getChunkSize() == null) {
            requestBody.setChunkSize(500);
        }
        if (requestBody.getChunkOverlap() == null) {
            requestBody.setChunkOverlap(20);
        }
        requestBody.setFileId(fileId);

        return fileService.chunk(requestBody);
    }

    /**
     * 获取分片信息
     * @param fileId 文件ID
     * @return
     */
    @GetMapping("/list/{fileId}")
    public ChunkListVO list(@PathVariable String fileId) {
        log.info("[GET] /processing/catman/storage/chunk/list/{}", fileId);
        ChunkListDTO dto = ChunkListDTO.build(fileId);
        return chunkService.list(dto);
    }
}
