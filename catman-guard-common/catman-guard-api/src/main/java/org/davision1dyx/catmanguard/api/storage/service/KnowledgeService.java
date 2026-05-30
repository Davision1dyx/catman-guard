package org.davision1dyx.catmanguard.api.storage.service;

import org.davision1dyx.catmanguard.api.storage.dto.CreateKnowledgeDTO;
import org.davision1dyx.catmanguard.api.storage.dto.KnowledgeListDTO;
import org.davision1dyx.catmanguard.api.storage.dto.UpdateKnowledgeDTO;
import org.davision1dyx.catmanguard.api.storage.vo.CreateKnowledgeVO;
import org.davision1dyx.catmanguard.api.storage.vo.KnowledgeVO;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 知识库Service接口
 */
public interface KnowledgeService {

    /**
     * 获取知识库列表
     * @param dto 查询条件
     * @return 知识库列表
     */
    List<KnowledgeVO> listKnowledge(KnowledgeListDTO dto);

    /**
     * 创建知识库
     * @param dto 创建参数
     * @return 创建结果
     */
    CreateKnowledgeVO createKnowledge(CreateKnowledgeDTO dto);

    /**
     * 更新知识库
     * @param knowledgeId 知识库ID
     * @param dto 更新参数
     * @return 更新后的知识库信息
     */
    KnowledgeVO updateKnowledge(String knowledgeId, UpdateKnowledgeDTO dto);

    /**
     * 删除知识库
     * @param knowledgeId 知识库ID
     */
    void deleteKnowledge(String knowledgeId);

    /**
     * 更新知识库文件数量
     * @param knowledgeId 知识库ID
     * @param delta 变化量（增加为正，减少为负）
     */
    void updateFileCount(String knowledgeId, int delta);

    /**
     * 更新知识库分片数量
     * @param knowledgeId 知识库ID
     * @param delta 变化量（增加为正，减少为负）
     */
    void updateChunkCount(String knowledgeId, int delta);
}