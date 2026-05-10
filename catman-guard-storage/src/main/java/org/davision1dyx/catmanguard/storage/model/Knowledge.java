package org.davision1dyx.catmanguard.storage.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.davision1dyx.catmanguard.datasource.entity.BaseModel;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 知识库模型
 */
@Data
@TableName("knowledge")
public class Knowledge extends BaseModel {

    /**
     * 知识库唯一标识(UUID)
     */
    @TableField("knowledge_id")
    private String knowledgeId;

    /**
     * 知识库名称
     */
    @TableField("name")
    private String name;

    /**
     * 知识库描述
     */
    @TableField("description")
    private String description;

    /**
     * 知识库类型: product-产品文档, technical-技术文档, operation-运维文档, training-培训资料
     */
    @TableField("type")
    private String type;

    /**
     * 文件数量
     */
    @TableField("file_count")
    private Integer fileCount;

    /**
     * 分片数量
     */
    @TableField("chunk_count")
    private Integer chunkCount;

    /**
     * 状态: ACTIVE-活跃, MAINTENANCE-维护中
     */
    @TableField("status")
    private String status;
}