package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 工单列表响应VO
 */
@Data
public class IssueListVO {

    /**
     * 工单列表
     */
    private List<IssueVO> list;

    /**
     * 总数
     */
    private Long total;
}