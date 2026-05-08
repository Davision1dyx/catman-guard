package org.davision1dyx.catmanguard.api.admin.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 创建值班人员请求DTO
 */
@Data
public class CreateStaffDTO {

    /**
     * 人员ID（可选，不填则自动生成）
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
}