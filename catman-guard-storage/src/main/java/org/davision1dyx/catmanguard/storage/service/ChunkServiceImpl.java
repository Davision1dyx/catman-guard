package org.davision1dyx.catmanguard.storage.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkEmbedDTO;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkListDTO;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkQueryDTO;
import org.davision1dyx.catmanguard.api.storage.dto.EmbedSyncDTO;
import org.davision1dyx.catmanguard.api.storage.service.ChunkService;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkEmbedVO;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkListVO;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkQueryVO;
import org.davision1dyx.catmanguard.api.storage.vo.EmbedSyncVO;
import org.davision1dyx.catmanguard.storage.convertor.FileChunkConvertor;
import org.davision1dyx.catmanguard.storage.mapper.FileChunkMapper;
import org.davision1dyx.catmanguard.storage.mapper.FileInfoMapper;
import org.davision1dyx.catmanguard.storage.model.FileChunk;
import org.davision1dyx.catmanguard.storage.model.FileInfo;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Davison
 * @date 2026-05-04
 * @description
 */
@Slf4j
@Service
public class ChunkServiceImpl extends ServiceImpl<FileChunkMapper, FileChunk> implements ChunkService {

    private final VectorStore vectorStore;
    private final FileInfoMapper fileInfoMapper;

    public ChunkServiceImpl(VectorStore vectorStore, FileInfoMapper fileInfoMapper) {
        this.vectorStore = vectorStore;
        this.fileInfoMapper = fileInfoMapper;
    }

    @Override
    public ChunkQueryVO query(ChunkQueryDTO chunkQueryDTO) {
        LambdaQueryWrapper<FileChunk> queryWrapper = new LambdaQueryWrapper<FileChunk>()
                .eq(FileChunk::getFileId, chunkQueryDTO.getFileId());
        if (chunkQueryDTO.getChunkIndex() != null) {
            queryWrapper.eq(FileChunk::getChunkIndex, chunkQueryDTO.getChunkIndex());
        }
        FileChunk fileChunk = getOne(queryWrapper);
        return FileChunkConvertor.INSTANCE.mapToVO(fileChunk);
    }

    @Override
    public ChunkListVO list(ChunkListDTO chunkListDTO) {
        log.info("开始查询分片列表, fileId: {}", chunkListDTO.getFileId());

        List<FileChunk> chunks = list(new LambdaQueryWrapper<FileChunk>()
                .eq(FileChunk::getFileId, chunkListDTO.getFileId()));

        List<ChunkListVO.ChunkItemVO> chunkItems = chunks.stream().map(c ->
                ChunkListVO.ChunkItemVO.build(
                        c.getChunkId(),
                        c.getContent(),
                        c.getMetaData(),
                        c.getChunkIndex(),
                        (long) c.getContent().length()
                )
        ).toList();

        int maxChunkSize = chunks.stream().mapToInt(c -> c.getContent().length()).max().orElse(0);
        int minChunkSize = chunks.stream().mapToInt(c -> c.getContent().length()).min().orElse(0);
        int avgChunkSize = (int) chunks.stream().mapToInt(c -> c.getContent().length()).average().orElse(0);

        return ChunkListVO.build(chunkItems, maxChunkSize, minChunkSize, avgChunkSize);
    }

    @Override
    public ChunkEmbedVO embed(ChunkEmbedDTO chunkEmbedDTO) {
        try {
            List<FileChunk> fileChunks = list(new LambdaQueryWrapper<FileChunk>()
                    .eq(FileChunk::getFileId, chunkEmbedDTO.getFileId()));
            List<Document> documents = fileChunks.stream().map(fileChunk -> {
                String metaData = fileChunk.getMetaData();
                Map<String, Object> metaObject = new HashMap<>();
                if (metaData != null && !metaData.isBlank()) {
                    metaObject = JSON.parseObject(metaData, new TypeReference<>() {
                    });
                }
                return new Document(fileChunk.getChunkId(), fileChunk.getContent(), metaObject);
            }).toList();

            for (int i = 0; i < documents.size(); i += 9) {
                List<Document> batches = documents.subList(i, Math.min(i + 9, documents.size()));
                vectorStore.add(batches);
                log.info("已添加 {} 个向量, 共 {} 个向量", i + batches.size(), documents.size());
            }
            return ChunkEmbedVO.success(documents.size());
        } catch (Exception e) {
            log.error("向量添加失败", e);
            return ChunkEmbedVO.fail();
        }
    }

    @Override
    public EmbedSyncVO sync(EmbedSyncDTO embedSyncDTO) {
        log.info("开始同步向量库, knowledgeId: {}", embedSyncDTO.getKnowledgeId());

        LambdaQueryWrapper<FileInfo> fileQueryWrapper = new LambdaQueryWrapper<>();
        fileQueryWrapper.eq(FileInfo::getStatus, "CHUNKED");
        List<FileInfo> fileInfos = fileInfoMapper.selectList(fileQueryWrapper);

        int syncedCount = 0;
        for (FileInfo fileInfo : fileInfos) {
            ChunkEmbedDTO dto = new ChunkEmbedDTO();
            dto.setFileId(fileInfo.getFileId());

            ChunkEmbedVO vo = embed(dto);
            if (vo.getSuccess()) {
                syncedCount += vo.getChunkSize();
                fileInfo.setStatus("VECTOR_STORED");
                fileInfoMapper.updateById(fileInfo);
            }
        }

        return EmbedSyncVO.build(syncedCount);
    }
}