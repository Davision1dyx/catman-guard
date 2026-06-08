package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Davison
 * @date 2026-06-02
 * @description 热点工单VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotIssueVO {

    /**
     * 热点关键词
     */
    private String keyword;

    /**
     * 相关工单数量
     */
    private Integer count;

    /**
     * 热点级别：HIGH/MEDIUM/LOW
     */
    private String level;

    /**
     * 相关工单列表
     */
    private List<IssueVO> issues;

    /**
     * 趋势：INCREASING/DECREASING/STABLE
     */
    private String trend;
}