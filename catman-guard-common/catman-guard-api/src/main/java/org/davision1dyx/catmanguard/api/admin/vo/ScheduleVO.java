package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 排班VO
 */
@Data
public class ScheduleVO {

    /**
     * 排班ID
     */
    private String id;

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 开始日期
     */
    private String startDate;

    /**
     * 结束日期
     */
    private String endDate;

    /**
     * 是否激活
     */
    private Boolean isActive;
}