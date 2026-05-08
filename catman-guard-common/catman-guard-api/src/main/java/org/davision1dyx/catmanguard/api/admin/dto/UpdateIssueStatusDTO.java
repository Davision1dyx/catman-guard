package org.davision1dyx.catmanguard.api.admin.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 更新工单状态请求DTO
 */
@Data
public class UpdateIssueStatusDTO {

    /**
     * 状态
     */
    private String status;
}