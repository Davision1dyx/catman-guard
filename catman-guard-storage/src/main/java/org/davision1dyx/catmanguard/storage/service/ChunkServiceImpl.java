package org.davision1dyx.catmanguard.storage.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkEmbedDTO;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkQueryDTO;
import org.davision1dyx.catmanguard.api.storage.service.ChunkService;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkEmbedVO;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkQueryVO;
import org.davision1dyx.catmanguard.storage.convertor.FileChunkConvertor;
import org.davision1dyx.catmanguard.storage.mapper.FileChunkMapper;
import org.davision1dyx.catmanguard.storage.model.FileChunk;
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

    public ChunkServiceImpl(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
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
}
