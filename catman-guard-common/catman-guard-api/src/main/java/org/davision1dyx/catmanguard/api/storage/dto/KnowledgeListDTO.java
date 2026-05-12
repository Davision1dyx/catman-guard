package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 知识库列表查询DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KnowledgeListDTO {

    /**
     * 搜索关键词
     */
    private String search;

    private String knowledgeId;
}