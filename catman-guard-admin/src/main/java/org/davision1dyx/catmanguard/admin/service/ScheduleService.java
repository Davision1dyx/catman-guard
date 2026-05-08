package org.davision1dyx.catmanguard.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.davision1dyx.catmanguard.admin.model.Schedule;
import org.davision1dyx.catmanguard.api.admin.dto.CreateScheduleDTO;
import org.davision1dyx.catmanguard.api.admin.dto.ScheduleListDTO;
import org.davision1dyx.catmanguard.api.admin.vo.CreateScheduleVO;
import org.davision1dyx.catmanguard.api.admin.vo.ScheduleListVO;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 排班Service接口
 */
public interface ScheduleService extends IService<Schedule> {

    /**
     * 获取排班列表
     * @param dto 查询条件
     * @return 排班列表
     */
    ScheduleListVO listSchedule(ScheduleListDTO dto);

    /**
     * 创建排班
     * @param dto 创建参数
     * @return 创建结果
     */
    CreateScheduleVO createSchedule(CreateScheduleDTO dto);
}
