package org.davision1dyx.catmanguard.admin.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.davision1dyx.catmanguard.datasource.entity.BaseModel;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 值班人员模型
 */
@Data
@TableName("staff")
public class Staff extends BaseModel {

    /**
     * 人员ID
     */
    @TableField("staff_id")
    private String staffId;

    /**
     * 姓名
     */
    @TableField("name")
    private String name;

    /**
     * 邮箱
     */
    @TableField("email")
    private String email;

    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 分组ID
     */
    @TableField("group_id")
    private String groupId;

    /**
     * 分组名称
     */
    @TableField("group_name")
    private String groupName;

    /**
     * 状态：ACTIVE/ON_LEAVE
     */
    @TableField("status")
    private String status;
}
