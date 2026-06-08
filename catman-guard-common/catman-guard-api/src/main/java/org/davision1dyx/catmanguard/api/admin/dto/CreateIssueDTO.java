package org.davision1dyx.catmanguard.api.admin.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 创建工单请求DTO
 */
@Data
public class CreateIssueDTO {

    /**
     * 标题
     */
    private String title;

    /**
     * 描述
     */
    private String description;

    /**
     * 类型
     */
    private String type;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 提交人ID
     */
    private String submitterId;

    /**
     * 提交人姓名
     */
    private String submitterName;

    /**
     * 提交人邮箱
     */
    private String submitterEmail;

    /**
     * 关联知识库ID（可选，默认关联工单知识库）
     */
    private String knowledgeId;
}