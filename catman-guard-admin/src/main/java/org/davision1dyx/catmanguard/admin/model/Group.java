package org.davision1dyx.catmanguard.admin.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.davision1dyx.catmanguard.datasource.entity.BaseModel;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 分组模型
 */
@Data
@TableName("staff_group")
public class Group extends BaseModel {

    /**
     * 分组ID
     */
    @TableField("group_id")
    private String groupId;

    /**
     * 分组名称
     */
    @TableField("name")
    private String name;
}
