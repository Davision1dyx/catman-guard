package org.davision1dyx.catmanguard.admin.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.davision1dyx.catmanguard.datasource.entity.BaseModel;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 排班模型
 */
@Data
@TableName("schedule")
public class Schedule extends BaseModel {

    /**
     * 排班ID
     */
    @TableField("schedule_id")
    private String scheduleId;

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
     * 开始日期
     */
    @TableField("start_date")
    private String startDate;

    /**
     * 结束日期
     */
    @TableField("end_date")
    private String endDate;

    /**
     * 是否启用
     */
    @TableField("is_active")
    private Boolean isActive;
}
