package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 数据看板趋势VO
 */
@Data
public class DashboardTrendVO {

    /**
     * 标签列表
     */
    private List<String> labels;

    /**
     * 对话数据
     */
    private List<Integer> conversations;

    /**
     * 工单数据
     */
    private List<Integer> tickets;
}