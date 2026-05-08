package org.davision1dyx.catmanguard.api.admin.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 分配工单请求DTO
 */
@Data
public class AssignIssueDTO {

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
}