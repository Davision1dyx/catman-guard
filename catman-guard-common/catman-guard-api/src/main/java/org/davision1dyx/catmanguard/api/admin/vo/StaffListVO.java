package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 值班人员列表响应VO
 */
@Data
public class StaffListVO {

    /**
     * 值班人员列表
     */
    private List<StaffVO> list;

    /**
     * 总数
     */
    private Long total;
}
