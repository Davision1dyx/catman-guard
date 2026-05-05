package org.davision1dyx.catmanguard.api.storage.service;

import org.davision1dyx.catmanguard.api.storage.dto.RetrievalDTO;
import org.davision1dyx.catmanguard.api.storage.vo.RetrievalVO;

/**
 * @author Davison
 * @date 2026-05-05
 * @description 检索服务
 */
public interface RetrievalService {
    /**
     * 根据query检索向量文档
     * @param retrievalDTO 检索DTO
     * @return 检索VO
     */
    RetrievalVO retrieve(RetrievalDTO retrievalDTO);
}
