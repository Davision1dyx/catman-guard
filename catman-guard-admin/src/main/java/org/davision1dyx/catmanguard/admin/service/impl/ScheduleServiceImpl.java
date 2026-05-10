package org.davision1dyx.catmanguard.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.convertor.ScheduleConvertor;
import org.davision1dyx.catmanguard.admin.mapper.ScheduleMapper;
import org.davision1dyx.catmanguard.admin.model.Schedule;
import org.davision1dyx.catmanguard.admin.service.ScheduleService;
import org.davision1dyx.catmanguard.api.admin.dto.CreateScheduleDTO;
import org.davision1dyx.catmanguard.api.admin.dto.ScheduleListDTO;
import org.davision1dyx.catmanguard.api.admin.vo.CreateScheduleVO;
import org.davision1dyx.catmanguard.api.admin.vo.ScheduleListVO;
import org.davision1dyx.catmanguard.api.admin.vo.ScheduleVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 排班Service实现
 */
@Slf4j
@Service
public class ScheduleServiceImpl extends ServiceImpl<ScheduleMapper, Schedule> implements ScheduleService {

    @Override
    public ScheduleListVO listSchedule(ScheduleListDTO dto) {
        log.info("Listing schedule with startDate: {}, endDate: {}", dto.getStartDate(), dto.getEndDate());
        
        LambdaQueryWrapper<Schedule> queryWrapper = new LambdaQueryWrapper<>();
        if (dto.getStartDate() != null && !dto.getStartDate().isEmpty()) {
            queryWrapper.ge(Schedule::getStartDate, dto.getStartDate());
        }
        if (dto.getEndDate() != null && !dto.getEndDate().isEmpty()) {
            queryWrapper.le(Schedule::getEndDate, dto.getEndDate());
        }
        
        List<Schedule> scheduleList = list(queryWrapper);
        
        List<ScheduleVO> voList = scheduleList.stream()
                .map(ScheduleConvertor.INSTANCE::mapToVO)
                .collect(Collectors.toList());
        
        ScheduleListVO result = new ScheduleListVO();
        result.setList(voList);
        return result;
    }

    @Override
    public CreateScheduleVO createSchedule(CreateScheduleDTO dto) {
        log.info("Creating schedule for group: {}", dto.getGroupId());
        
        Schedule schedule = new Schedule();
        schedule.setScheduleId("sched-" + UUID.randomUUID().toString().substring(0, 8));
        schedule.setStartDate(dto.getStartDate());
        schedule.setEndDate(dto.getEndDate());
        schedule.setGroupId(dto.getGroupId());
        
        if (dto.getGroupName() != null) {
            schedule.setGroupName(dto.getGroupName());
        } else if (dto.getGroupId() != null) {
            schedule.setGroupName("分组" + dto.getGroupId().substring(dto.getGroupId().length() - 1));
        }
        
        schedule.setIsActive(true);
        save(schedule);
        
        CreateScheduleVO result = new CreateScheduleVO();
        result.setScheduleId(schedule.getScheduleId());
        return result;
    }
}
