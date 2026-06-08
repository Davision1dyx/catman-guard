package org.davision1dyx.catmanguard.storage.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.IssueFileCreateDTO;
import org.davision1dyx.catmanguard.api.storage.service.IssueFileService;
import org.davision1dyx.catmanguard.storage.mapper.FileChunkMapper;
import org.davision1dyx.catmanguard.storage.mapper.FileInfoMapper;
import org.davision1dyx.catmanguard.storage.model.FileChunk;
import org.davision1dyx.catmanguard.storage.model.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * @author Davison
 * @date 2026-06-02
 * @description 工单文件服务实现
 */
@Slf4j
@Service
public class IssueFileServiceImpl implements IssueFileService {

    private static final String FILE_TYPE_ISSUE = "issue";
    private static final String STATUS_VECTOR_STORED = "VECTOR_STORED";

    private final FileInfoMapper fileInfoMapper;
    private final FileChunkMapper fileChunkMapper;

    public IssueFileServiceImpl(FileInfoMapper fileInfoMapper, FileChunkMapper fileChunkMapper) {
        this.fileInfoMapper = fileInfoMapper;
        this.fileChunkMapper = fileChunkMapper;
    }

    @Override
    public boolean existsFile(String fileId) {
        if (fileId == null || fileId.isEmpty()) {
            return false;
        }
        
        LambdaQueryWrapper<FileInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FileInfo::getFileId, fileId);
        return fileInfoMapper.selectOne(queryWrapper) != null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createIssueFile(IssueFileCreateDTO dto) {
        log.info("Creating issue file: {}", dto.getFileId());
        
        FileInfo fileInfo = new FileInfo();
        fileInfo.setFileId(dto.getFileId());
        fileInfo.setFileName(dto.getFileName());
        fileInfo.setFileTitle(dto.getFileTitle());
        fileInfo.setFileType(FILE_TYPE_ISSUE);
        fileInfo.setStatus(STATUS_VECTOR_STORED);
        fileInfo.setKnowledgeId(dto.getKnowledgeId());
        fileInfo.setDescription(dto.getDescription());
        
        fileInfoMapper.insert(fileInfo);
        
        log.info("Successfully created issue file: {}", dto.getFileId());
        return dto.getFileId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createIssueChunk(String fileId, String content, String metaData) {
        log.info("Creating issue chunk for file: {}", fileId);
        
        String chunkId = "chunk-" + UUID.randomUUID().toString();
        
        FileChunk chunk = new FileChunk();
        chunk.setChunkId(chunkId);
        chunk.setFileId(fileId);
        chunk.setChunkIndex(0);
        chunk.setContent(content);
        chunk.setMetaData(metaData);
        
        fileChunkMapper.insert(chunk);
        
        log.info("Successfully created issue chunk: {} for file: {}", chunkId, fileId);
        return chunkId;
    }
}