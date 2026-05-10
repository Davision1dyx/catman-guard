package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 更新知识库DTO
 */
@Data
public class UpdateKnowledgeDTO {

    /**
     * 知识库名称
     */
    private String name;

    /**
     * 知识库描述
     */
    private String description;

    /**
     * 知识库类型: product-产品文档, technical-技术文档, operation-运维文档, training-培训资料
     */
    private String type;
}