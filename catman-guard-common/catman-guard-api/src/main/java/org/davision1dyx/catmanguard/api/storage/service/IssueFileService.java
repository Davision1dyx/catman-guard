package org.davision1dyx.catmanguard.api.storage.service;

import org.davision1dyx.catmanguard.api.storage.dto.IssueFileCreateDTO;

/**
 * @author Davison
 * @date 2026-06-02
 * @description 工单文件服务接口
 */
public interface IssueFileService {

    /**
     * 检查工单文件是否已存在
     * @param fileId 文件ID
     * @return 是否存在
     */
    boolean existsFile(String fileId);

    /**
     * 创建工单虚拟文件
     * @param dto 创建参数
     * @return 文件ID
     */
    String createIssueFile(IssueFileCreateDTO dto);

    /**
     * 创建工单文件分片
     * @param fileId 文件ID
     * @param content 分片内容
     * @param metaData 元数据JSON
     * @return 分片ID
     */
    String createIssueChunk(String fileId, String content, String metaData);
}