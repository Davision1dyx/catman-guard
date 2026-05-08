package org.davision1dyx.catmanguard.api.admin.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 更新工单请求DTO
 */
@Data
public class UpdateIssueDTO {

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
}