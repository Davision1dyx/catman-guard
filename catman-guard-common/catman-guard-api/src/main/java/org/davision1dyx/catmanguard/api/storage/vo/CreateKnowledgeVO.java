package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 创建知识库响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateKnowledgeVO {

    /**
     * 创建的知识库ID
     */
    private String knowledgeId;
}