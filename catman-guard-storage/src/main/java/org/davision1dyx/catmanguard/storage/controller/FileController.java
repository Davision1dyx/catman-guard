package org.davision1dyx.catmanguard.storage.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkQueryDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileListDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileUploadDTO;
import org.davision1dyx.catmanguard.api.storage.service.ChunkService;
import org.davision1dyx.catmanguard.api.storage.service.FileService;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkQueryVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileListVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileUploadVO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-01
 * @description 文件接口
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/storage/file")
public class FileController {

    private final FileService fileService;
    private final ChunkService chunkService;

    public FileController(FileService fileService, ChunkService chunkService) {
        this.fileService = fileService;
        this.chunkService = chunkService;
    }

    /**
     * 文件上传
     * @param file
     * @param title
     * @param description
     * @return
     */
    @PostMapping("/upload")
    public FileUploadVO upload(@RequestParam MultipartFile file,
                               @RequestParam String title,
                               @RequestParam String knowledgeId,
                               @RequestParam(required = false) String softwareVersion,
                               @RequestParam(required = false) String feature,
                               @RequestParam(required = false) String microservice,
                               @RequestParam(required = false) String description) {
        log.info("[POST] /processing/catman/storage/file/upload, file: {}, title: {}, description: {}",
                file.getOriginalFilename(), title, description);
        FileUploadDTO fileUploadDTO = FileUploadDTO.build(file, title, description, knowledgeId);
        return fileService.upload(fileUploadDTO);
    }

    /**
     * 获取文件列表
     * @param knowledgeId 知识库ID（当前未使用，预留）
     * @param search 搜索关键词
     * @param fileType 文件类型筛选
     * @param status 状态筛选
     * @return
     */
    @GetMapping("/list/{knowledgeId}")
    public List<FileListVO> list(@PathVariable String knowledgeId,
                                 @RequestParam(required = false) String search,
                                 @RequestParam(required = false) String fileType,
                                 @RequestParam(required = false) String status) {
        log.info("[GET] /processing/catman/storage/file/list/{}", knowledgeId);
        FileListDTO fileListDTO = FileListDTO.build(knowledgeId, search, fileType, status);
        return fileService.list(fileListDTO);
    }

    /**
     * 删除文件
     * @param fileId 文件ID
     */
    @DeleteMapping("/{fileId}")
    public void delete(@PathVariable String fileId) {
        log.info("[DELETE] /processing/catman/storage/file/{}", fileId);
        fileService.delete(fileId);
    }

    @GetMapping("/queryChunk")
    public ChunkQueryVO queryChunk(@RequestParam String fileId) {
        log.info("[GET] /processing/catman/storage/file/querySplit, fileId: {}", fileId);
        ChunkQueryDTO dto = new ChunkQueryDTO();
        dto.setFileId(fileId);
        return chunkService.query(dto);
    }
}
