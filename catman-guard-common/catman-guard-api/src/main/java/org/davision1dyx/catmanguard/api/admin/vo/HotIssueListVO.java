package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Davison
 * @date 2026-06-02
 * @description 热点工单列表VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotIssueListVO {

    /**
     * 热点工单列表
     */
    private List<HotIssueVO> list;

    /**
     * 总数
     */
    private Integer total;
}