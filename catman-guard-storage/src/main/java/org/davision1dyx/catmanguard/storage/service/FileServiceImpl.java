package org.davision1dyx.catmanguard.storage.service;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.conversation.service.MultiModalService;
import org.davision1dyx.catmanguard.api.storage.dto.*;
import org.davision1dyx.catmanguard.api.storage.service.FileService;
import org.davision1dyx.catmanguard.api.storage.service.KnowledgeService;
import org.davision1dyx.catmanguard.api.storage.vo.*;
import org.davision1dyx.catmanguard.base.constant.CommonConstant;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.base.util.FileUtil;
import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.davision1dyx.catmanguard.file.properties.FileProperties;
import org.davision1dyx.catmanguard.storage.convertor.FileChunkConvertor;
import org.davision1dyx.catmanguard.storage.convertor.FileInfoConvertor;
import org.davision1dyx.catmanguard.storage.enums.FileStatus;
import org.davision1dyx.catmanguard.storage.handle.reader.ReaderHandler;
import org.davision1dyx.catmanguard.storage.handle.recognition.RecognitionHandler;
import org.davision1dyx.catmanguard.storage.handle.storage.StorageHandler;
import org.davision1dyx.catmanguard.storage.handle.transform.OverlapParagraphTextSplitHandler;
import org.davision1dyx.catmanguard.storage.mapper.FileInfoMapper;
import org.davision1dyx.catmanguard.storage.model.FileChunk;
import org.davision1dyx.catmanguard.storage.model.FileInfo;
import org.davision1dyx.catmanguard.storage.pojo.StorageHandleInfo;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
    private final ReaderHandler readerHandler;
    private final MultiModalService multiModalService;
    private final ChunkServiceImpl fileChunkService;
    private final KnowledgeService knowledgeService;

    @Autowired(required = false)
    private MinioClient minioClient;

    public FileServiceImpl(FileProperties fileProperties, StorageHandler storageHandler, RecognitionHandler recognitionHandler, ReaderHandler readerHandler, MultiModalService multiModalService, ChunkServiceImpl fileChunkService, KnowledgeService knowledgeService) {
        this.fileProperties = fileProperties;
        this.storageHandler = storageHandler;
        this.recognitionHandler = recognitionHandler;
        this.readerHandler = readerHandler;
        this.multiModalService = multiModalService;
        this.fileChunkService = fileChunkService;
        this.knowledgeService = knowledgeService;
    }

    @Override
    public FileUploadVO upload(FileUploadDTO fileUploadDTO) {
        log.info("开始上传文件: {}", fileUploadDTO.getFile().getOriginalFilename());

        List<KnowledgeVO> knowledgeVOS = knowledgeService.listKnowledge(KnowledgeListDTO.builder().knowledgeId(fileUploadDTO.getKnowledgeId()).build());
        if (knowledgeVOS.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "知识库不存在");
        }

        long size = fileUploadDTO.getFile().getSize();
        // 1. 源文件存储到存储介质
        FileMode mode = fileProperties.getMode();
        StorageHandleInfo storageHandleInfo = storageHandler.handleUpload(fileUploadDTO.getFile(), mode);

        // 2. 保存文件信息到数据库
        FileInfo fileInfo = FileInfoConvertor.INSTANCE.mapToModel(fileUploadDTO, storageHandleInfo.getFileName(),
                storageHandleInfo.getUrl(), mode);
        save(fileInfo);
        return FileUploadVO.build(fileInfo.getFileId(), fileInfo.getUrl(), fileInfo.getStatus(), size);
    }

    @Override
    public List<FileListVO> list(FileListDTO fileListDTO) {
        log.info("开始查询文件列表, knowledgeId: {}, search: {}, fileType: {}, status: {}",
                fileListDTO.getKnowledgeId(), fileListDTO.getSearch(),
                fileListDTO.getFileType(), fileListDTO.getStatus());

        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        if (fileListDTO.getKnowledgeId() != null && !fileListDTO.getKnowledgeId().isEmpty()) {
            queryWrapper.eq(FileInfo::getKnowledgeId, fileListDTO.getKnowledgeId());
        }
        if (fileListDTO.getSearch() != null && !fileListDTO.getSearch().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper.like(FileInfo::getFileTitle, fileListDTO.getSearch())
                    .or().like(FileInfo::getFileName, fileListDTO.getSearch()));
        }
        if (fileListDTO.getFileType() != null && !fileListDTO.getFileType().isEmpty()) {
            queryWrapper.eq(FileInfo::getFileType, fileListDTO.getFileType());
        }
        if (fileListDTO.getStatus() != null && !fileListDTO.getStatus().isEmpty()) {
            queryWrapper.eq(FileInfo::getStatus, fileListDTO.getStatus());
        }

        List<FileInfo> fileInfoList = list(queryWrapper);

        List<FileListVO> data = fileInfoList.stream().map(f -> {
            return FileListVO.build(
                    f.getFileId(),
                    f.getFileTitle(),
                    f.getFileName(),
                    "",
                    f.getExtension(),
                    f.getDescription(),
                    "",
                    f.getStatus(),
                    0L,
                    f.getCreateTime(),
                    f.getUpdateTime()
            );
        }).collect(java.util.stream.Collectors.toList());

        return data;
    }

    @Override
    public void delete(String fileId) {
        log.info("开始删除文件, fileId: {}", fileId);

        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getFileId, fileId);
        remove(queryWrapper);
        // TODO 对storage进行删除
    }

    public FileRecognizeVO recognize(String fileId) throws IOException {
        // 0. 更新状态
        update(new LambdaUpdateWrapper<FileInfo>().eq(FileInfo::getFileId, fileId).set(FileInfo::getStatus, FileStatus.CONVERTING.name()));

        FileInfo fileInfo = getOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getFileId, fileId));

        InputStream inputStream = getFileInputStream(fileInfo.getUrl(), fileInfo.getStorageType());

        String recognizedPath = recognitionHandler.handle(inputStream, fileInfo.getFileName());

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
        String baseObjectName = "CONVERT_" + fileInfo.getFileTitle() + "/";

        for (Path imagePath : imageFiles) {
            String imageName = imagePath.getFileName().toString();
            byte[] imageBytes = FileUtil.readFileToBytes(imagePath);
            String contentType = FileUtil.getImageContentType(imageName);
            String objectName = baseObjectName + "images/" + imageName;
            StorageHandleInfo storageHandleInfo = storageHandler.handleUpload(imageBytes, objectName, contentType, fileMode);
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
        StorageHandleInfo storageHandleInfo = storageHandler.handleUpload(processedMdContent.getBytes(StandardCharsets.UTF_8), mdObjectName,
                "text/markdown", fileMode);
        String convertedUrl = storageHandleInfo.getUrl();
        log.info("Markdown 文件已上传, url: {}", convertedUrl);

        // 3. 更新状态
        update(new LambdaUpdateWrapper<FileInfo>()
                .eq(FileInfo::getFileId, fileId)
                .set(FileInfo::getConvertedUrl, convertedUrl)
                .set(FileInfo::getStatus, FileStatus.CONVERTED.name()));

        return FileRecognizeVO.build(convertedUrl);
    }


    @Override
    public FileRecognizeVO recognize(FileRecognizeDTO fileRecognizeDTO) throws IOException {
        // 只有PDF和DOC才需要识别

        // 0. 更新状态
        update(new LambdaUpdateWrapper<FileInfo>().eq(FileInfo::getFileId, fileRecognizeDTO.getFileId()).set(FileInfo::getStatus, FileStatus.CONVERTING.name()));
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
            StorageHandleInfo storageHandleInfo = storageHandler.handleUpload(imageBytes, objectName, contentType, fileMode);
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
        StorageHandleInfo storageHandleInfo = storageHandler.handleUpload(processedMdContent.getBytes(StandardCharsets.UTF_8), mdObjectName,
                "text/markdown", fileMode);
        String convertedUrl = storageHandleInfo.getUrl();
        log.info("Markdown 文件已上传, url: {}", convertedUrl);

        // 3. 更新状态
        update(new LambdaUpdateWrapper<FileInfo>()
                .eq(FileInfo::getFileId, fileRecognizeDTO.getFileId())
                .set(FileInfo::getConvertedUrl, convertedUrl)
                .set(FileInfo::getStatus, FileStatus.CONVERTED.name()));

        return FileRecognizeVO.build(convertedUrl);
    }

    @Override
    public FileSplitVO split(FileSplitDTO fileSplitDTO) {
        FileInfo fileInfo = getOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getFileId, fileSplitDTO.getFileId()));
        if (fileInfo == null) {
            throw new BizException(ErrorCode.ERROR, "文件不存在");
        }
        if (!Objects.equals(fileInfo.getStatus(), FileStatus.CONVERTED.name())) {
            throw new BizException(ErrorCode.ERROR, "文件未完成转换");
        }
        log.info("开始分割文件: {}", fileInfo.getFileName());
        FileMode fileMode = fileProperties.getMode();
        // 1. 下载文件
        byte[] fileBytes = storageHandler.handleDownload(fileInfo.getConvertedUrl(), fileMode);

        // 2. 切分文件 // TODO 待完善切分，当前读取文件内容，只需要读取md格式
        List<Document> documents = readerHandler.handle(fileBytes, fileInfo.getFileType());
        List<Document> splitDocuments = new OverlapParagraphTextSplitHandler(500, 10).split(documents);
        log.info("文件已切分, 数量: {}", splitDocuments.size());

        // 3. 保存切分结果
        List<FileChunk> fileChunks = new ArrayList<>();
        for (int i = 0; i < splitDocuments.size(); i++) {
            Document document = splitDocuments.get(i);
            FileChunk fileChunk = FileChunkConvertor.INSTANCE.mapToModel(fileInfo.getFileId(), i, document.getText(),
                    JSON.toJSONString(document.getMetadata()), document.getId());
            fileChunks.add(fileChunk);
        }
        fileChunkService.saveBatch(fileChunks); // 性能不足时，考虑手写批量保存逻辑

        // 4. 更新状态
        fileInfo.setStatus(FileStatus.CHUNKED.name());
        updateById(fileInfo);

        return FileSplitVO.build(fileChunks.size());
    }

    @Override
    public ChunkVO chunk(FileSplitDTO fileSplitDTO) {
        log.info("开始执行分片, fileId: {}, chunkType: {}, chunkSize: {}, chunkOverlap: {}",
                fileSplitDTO.getFileId(), fileSplitDTO.getChunkType(),
                fileSplitDTO.getChunkSize(), fileSplitDTO.getChunkOverlap());

        // 文件识别
        try {
            recognize(fileSplitDTO.getFileId());
        } catch (Exception e) {
            throw new BizException(ErrorCode.ERROR, "文件识别失败", e);
        }

        split(fileSplitDTO);

        List<FileChunk> chunks = fileChunkService.list(new LambdaQueryWrapper<FileChunk>()
                .eq(FileChunk::getFileId, fileSplitDTO.getFileId()));

        List<ChunkVO.ChunkItemVO> chunkItems = chunks.stream().map(c ->
                ChunkVO.ChunkItemVO.build(c.getChunkId(), c.getContent(), (long) c.getContent().length())
        ).toList();

        return ChunkVO.build(fileSplitDTO.getFileId(), "CHUNKED", chunkItems);
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

    private InputStream getFileInputStream(String fileUrl, String storageType) {
        try {
            if (FileMode.Local.name().equals(storageType)) {
                return Files.newInputStream(Paths.get(fileUrl));
            } else if (FileMode.Minio.name().equals(storageType)) {
                String endpoint = fileProperties.getMinio().getEndpoint();
                String bucketName = fileProperties.getMinio().getBucketName();
                String objectName = fileUrl.replace(endpoint + CommonConstant.FILE_SEPARATOR + bucketName + CommonConstant.FILE_SEPARATOR, "");
                return minioClient.getObject(
                        GetObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
            } else {
                throw new BizException(ErrorCode.NO_FILE_TYPE_SUPPORT, "不支持的存储类型: " + storageType);
            }
        } catch (Exception e) {
            log.error("获取文件输入流失败, fileUrl: {}, storageType: {}", fileUrl, storageType, e);
            throw new BizException(ErrorCode.ERROR, "获取文件输入流失败");
        }
    }
}
