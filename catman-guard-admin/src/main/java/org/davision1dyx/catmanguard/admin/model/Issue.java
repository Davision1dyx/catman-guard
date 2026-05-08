package org.davision1dyx.catmanguard.admin.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.davision1dyx.catmanguard.datasource.entity.BaseModel;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 工单模型
 */
@Data
@TableName("issue")
public class Issue extends BaseModel {

    /**
     * 工单编号
     */
    @TableField("issue_id")
    private String issueId;

    /**
     * 工单标题
     */
    @TableField("title")
    private String title;

    /**
     * 工单描述
     */
    @TableField("description")
    private String description;

    /**
     * 工单类型：INCIDENT/REQUEST/PROBLEM/CHANGE
     */
    @TableField("type")
    private String type;

    /**
     * 优先级：HIGH/MEDIUM/LOW/CRITICAL
     */
    @TableField("priority")
    private String priority;

    /**
     * 状态：ASSIGNED/IN_PROGRESS/COMPLETED/VECTOR_STORED
     */
    @TableField("status")
    private String status;

    /**
     * 提交人ID
     */
    @TableField("submitter_id")
    private String submitterId;

    /**
     * 提交人姓名
     */
    @TableField("submitter_name")
    private String submitterName;

    /**
     * 提交人邮箱
     */
    @TableField("submitter_email")
    private String submitterEmail;

    /**
     * 负责人ID
     */
    @TableField("assignee_id")
    private String assigneeId;

    /**
     * 负责人姓名
     */
    @TableField("assignee_name")
    private String assigneeName;

    /**
     * 负责人邮箱
     */
    @TableField("assignee_email")
    private String assigneeEmail;
}
