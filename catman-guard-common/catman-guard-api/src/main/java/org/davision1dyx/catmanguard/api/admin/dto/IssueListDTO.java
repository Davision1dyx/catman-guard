package org.davision1dyx.catmanguard.api.admin.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 工单列表查询请求DTO
 */
@Data
public class IssueListDTO {

    /**
     * 搜索关键词
     */
    private String search;

    /**
     * 状态
     */
    private String status;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 处理人
     */
    private String assignee;

    /**
     * 类型
     */
    private String type;

    /**
     * 页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer size;
}