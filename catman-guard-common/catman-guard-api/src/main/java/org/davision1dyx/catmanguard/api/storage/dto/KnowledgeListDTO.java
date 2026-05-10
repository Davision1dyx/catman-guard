package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 知识库列表查询DTO
 */
@Data
public class KnowledgeListDTO {

    /**
     * 搜索关键词
     */
    private String search;
}