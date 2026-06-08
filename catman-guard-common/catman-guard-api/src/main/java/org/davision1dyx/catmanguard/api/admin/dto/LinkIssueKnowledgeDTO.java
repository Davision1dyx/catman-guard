package org.davision1dyx.catmanguard.api.admin.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-06-02
 * @description 工单关联知识库请求DTO
 */
@Data
public class LinkIssueKnowledgeDTO {

    /**
     * 知识库ID
     */
    private String knowledgeId;
}