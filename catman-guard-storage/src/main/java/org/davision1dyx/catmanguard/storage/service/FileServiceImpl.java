package org.davision1dyx.catmanguard.storage.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.conversation.service.MultiModalService;
import org.davision1dyx.catmanguard.api.storage.dto.FileRecognizeDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileSplitDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileUploadDTO;
import org.davision1dyx.catmanguard.api.storage.service.FileService;
import org.davision1dyx.catmanguard.api.storage.vo.FileRecognizeVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileSplitVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileUploadVO;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.base.util.FileUtil;
import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.davision1dyx.catmanguard.file.properties.FileProperties;
import org.davision1dyx.catmanguard.storage.convertor.FileInfoConvertor;
import org.davision1dyx.catmanguard.storage.handle.recognition.RecognitionHandler;
import org.davision1dyx.catmanguard.storage.handle.storage.StorageHandler;
import org.davision1dyx.catmanguard.storage.mapper.FileInfoMapper;
import org.davision1dyx.catmanguard.storage.model.FileInfo;
import org.davision1dyx.catmanguard.storage.pojo.StorageHandleInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author Davison
 * @date 2026-05-01
 * @description
 */
@Slf4j
@Component
public class FileServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileService {

    private final FileProperties fileProperties;
    private final StorageHandler storageHandler;
    private final RecognitionHandler recognitionHandler;
    private final MultiModalService multiModalService;

    public FileServiceImpl(FileProperties fileProperties, StorageHandler storageHandler, RecognitionHandler recognitionHandler, MultiModalService multiModalService) {
        this.fileProperties = fileProperties;
        this.storageHandler = storageHandler;
        this.recognitionHandler = recognitionHandler;
        this.multiModalService = multiModalService;
    }

    @Override
    public FileUploadVO upload(FileUploadDTO fileUploadDTO) {
        // 1. 源文件存储到存储介质
        FileMode mode = fileProperties.getMode();
        StorageHandleInfo storageHandleInfo = storageHandler.handle(fileUploadDTO.getFile(), mode);

        // 2. 保存文件信息到数据库
        FileInfo fileInfo = FileInfoConvertor.INSTANCE.mapToModel(fileUploadDTO, storageHandleInfo.getFileName(),
                storageHandleInfo.getUrl(), mode);
        save(fileInfo);
        return FileUploadVO.build(fileInfo.getFileId(), fileInfo.getUrl(), fileInfo.getStatus());
    }

    @Override
    public FileRecognizeVO recognize(FileRecognizeDTO fileRecognizeDTO) throws IOException {
        // 1. 文件OCR识别
        MultipartFile file = fileRecognizeDTO.getFile();
        String recognizedPath = recognitionHandler.handle(file);

        // 2. 文件存储
        FileMode fileMode = fileProperties.getMode();


        // 2.1查找所有的 md 文件和图片文件
        Path mdFile = null;
        java.util.List<Path> imageFiles = new java.util.ArrayList<>();

        try (Stream<Path> paths = Files.walk(Path.of(recognizedPath))) {
            for (Path path : paths.toList()) {
                if (Files.isRegularFile(path)) {
                    String fileName = path.getFileName().toString().toLowerCase();
                    if (fileName.endsWith(".md")) {
                        mdFile = path;
                    } else if (fileName.endsWith(".png") || fileName.endsWith(".jpg") ||
                            fileName.endsWith(".jpeg") || fileName.endsWith(".gif") ||
                            fileName.endsWith(".webp") || fileName.endsWith(".bmp")) {
                        imageFiles.add(path);
                    }
                }
            }
        }

        if (mdFile == null) {
            throw new BizException(ErrorCode.ERROR, "文件中没有找到 markdown 文件");
        }

        // 2.2 上传图片，建立本地文件名与图片url的映射
        log.info("找到 Markdown 文件: {}, 图片文件数量: {}", mdFile, imageFiles.size());
        Map<String, String> imageUrlMap = new HashMap<>();
        String baseObjectName = "CONVERT_" + fileRecognizeDTO.getFileTitle() + "/";

        for (Path imagePath : imageFiles) {
            String imageName = imagePath.getFileName().toString();
            byte[] imageBytes = FileUtil.readFileToBytes(imagePath);
            String contentType = FileUtil.getImageContentType(imageName);
            String objectName = baseObjectName + "images/" + imageName;
            StorageHandleInfo storageHandleInfo = storageHandler.handle(imageBytes, objectName, contentType, fileMode);
            String imageUrl = storageHandleInfo.getUrl();
            imageUrlMap.put(imageName, imageUrl);
            log.info("图片已上传 {} -> {}", imageName, imageUrl);
        }

        // 读取 md 文件内容
        String mdContent = FileUtil.readFileToString(mdFile);

        // 替换 md 中的图片地址为 MinIO 地址，并生成图片描述
        String processedMdContent = processMarkdownImages(mdContent, imageUrlMap);

        // 上传处理后的 md 文件到 MinIO
        String mdObjectName = baseObjectName + mdFile.getFileName().toString();
        StorageHandleInfo storageHandleInfo = storageHandler.handle(processedMdContent.getBytes(StandardCharsets.UTF_8), mdObjectName,
                "text/markdown", fileMode);
        log.info("Markdown 文件已上传, url: {}", storageHandleInfo.getUrl());

        return FileRecognizeVO.build(storageHandleInfo.getUrl());
    }

    @Override
    public FileSplitVO split(FileSplitDTO fileSplitDTO) {
        return null;
    }

    @Override
    public void download(String fileUrl) {

    }

    /**
     * 处理 Markdown 中的图片标签：替换地址并生成图片描述
     * 匹配格式: ![](xxx.png) 或 ![alt](xxx.png)
     */
    private String processMarkdownImages(String mdContent, java.util.Map<String, String> imageUrlMap) {
        // 匹配图片标签的正则表达式: ![alt](path)
        Pattern pattern = Pattern.compile("!\\[(.*?)\\]\\(([^)]+)\\)");
        Matcher matcher = pattern.matcher(mdContent);

        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String altText = matcher.group(1);
            String imagePath = matcher.group(2);

            // 提取图片文件名
            String imageName = Paths.get(imagePath).getFileName().toString();

            // 获取 MinIO 上的图片 URL
            String minioUrl = imageUrlMap.get(imageName);
            if (minioUrl == null) {
                // 如果找不到对应的 MinIO URL，保持原样
                log.warn("未找到图片 {} 对应的 MinIO URL", imageName);
                matcher.appendReplacement(result, Matcher.quoteReplacement(matcher.group(0)));
                continue;
            }

            // 生成图片描述（mock 实现）
            String imageDescription = multiModalService.generateImageDescription(minioUrl);

            // 构建新的图片标签: ![描述](minio_url)
            String newImageTag = "![" + imageDescription + "](" + minioUrl + ")";
            matcher.appendReplacement(result, Matcher.quoteReplacement(newImageTag));

            log.info("图片标签已处理: {} -> {}", imagePath, minioUrl);
        }
        matcher.appendTail(result);

        return result.toString();
    }
}
