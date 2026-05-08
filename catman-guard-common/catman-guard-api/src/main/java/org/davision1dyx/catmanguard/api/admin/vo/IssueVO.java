package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 工单VO
 */
@Data
public class IssueVO {

    /**
     * 工单ID
     */
    private String id;

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
     * 状态
     */
    private String status;

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
     * 处理人ID
     */
    private String assigneeId;

    /**
     * 处理人姓名
     */
    private String assigneeName;

    /**
     * 处理人邮箱
     */
    private String assigneeEmail;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}