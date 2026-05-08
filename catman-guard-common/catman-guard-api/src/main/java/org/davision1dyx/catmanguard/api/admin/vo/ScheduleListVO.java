package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 排班列表响应VO
 */
@Data
public class ScheduleListVO {

    /**
     * 排班列表
     */
    private List<ScheduleVO> list;
}