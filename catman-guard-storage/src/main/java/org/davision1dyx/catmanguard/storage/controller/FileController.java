package org.davision1dyx.catmanguard.storage.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.FileUploadDTO;
import org.davision1dyx.catmanguard.api.storage.service.FileService;
import org.davision1dyx.catmanguard.api.storage.vo.FileUploadVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    /**
     * 文件上传
     * @param file
     * @param title
     * @param description
     * @return
     */
    @GetMapping("/upload")
    public FileUploadVO upload(@RequestParam MultipartFile file,
                               @RequestParam String title,
                               @RequestParam String description) {
        log.info("[GET] /processing/catman/storage/file/upload, file: {}, title: {}, description: {}",
                file.getOriginalFilename(), title, description);
        FileUploadDTO fileUploadDTO = FileUploadDTO.build(file, title, description);
        return fileService.upload(fileUploadDTO);
    }
}
