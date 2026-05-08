package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 值班人员VO
 */
@Data
public class StaffVO {

    /**
     * 人员ID
     */
    private String id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 电话
     */
    private String phone;

    /**
     * 分组ID
     */
    private String groupId;

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 状态：ACTIVE/ON_LEAVE
     */
    private String status;

    /**
     * 加入时间
     */
    private LocalDateTime joinedTime;
}
