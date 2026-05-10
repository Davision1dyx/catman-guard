package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 知识库VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeVO {

    /**
     * 知识库ID
     */
    private String knowledgeId;

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

    /**
     * 文件数量
     */
    private Integer fileCount;

    /**
     * 分片数量
     */
    private Integer chunkCount;

    /**
     * 创建时间
     */
    private String createdTime;

    /**
     * 更新时间
     */
    private String updatedTime;
}