package org.davision1dyx.catmanguard.api.admin.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 更新值班人员请求DTO
 */
@Data
public class UpdateStaffDTO {

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
     * 状态：ACTIVE/ON_LEAVE
     */
    private String status;
}