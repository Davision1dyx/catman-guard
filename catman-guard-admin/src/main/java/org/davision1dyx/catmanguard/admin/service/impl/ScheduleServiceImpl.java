package org.davision1dyx.catmanguard.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.convertor.ScheduleConvertor;
import org.davision1dyx.catmanguard.admin.mapper.ScheduleMapper;
import org.davision1dyx.catmanguard.admin.model.Group;
import org.davision1dyx.catmanguard.admin.model.Schedule;
import org.davision1dyx.catmanguard.admin.service.GroupService;
import org.davision1dyx.catmanguard.admin.service.ScheduleService;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
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

    private final GroupService groupService;

    public ScheduleServiceImpl(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public ScheduleListVO listSchedule(ScheduleListDTO dto) {
        log.info("Listing schedule with startDate: {}, endDate: {}", dto.getStartDate(), dto.getEndDate());
        
        validateDateRange(dto.getStartDate(), dto.getEndDate());
        
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
        
        validateCreateSchedule(dto);
        Group group = validateAndGetGroup(dto.getGroupId());
        
        Schedule schedule = new Schedule();
        schedule.setScheduleId("sched-" + UUID.randomUUID().toString().substring(0, 8));
        schedule.setStartDate(dto.getStartDate());
        schedule.setEndDate(dto.getEndDate());
        schedule.setGroupId(dto.getGroupId());
        
        if (dto.getGroupName() != null) {
            schedule.setGroupName(dto.getGroupName());
        } else {
            schedule.setGroupName(group.getName());
        }
        
        schedule.setIsActive(true);
        save(schedule);
        
        CreateScheduleVO result = new CreateScheduleVO();
        result.setScheduleId(schedule.getScheduleId());
        return result;
    }
    
    private void validateDateRange(String startDate, String endDate) {
        if (startDate != null && endDate != null 
            && !startDate.isEmpty() && !endDate.isEmpty()
            && startDate.compareTo(endDate) > 0) {
            throw new BizException(ErrorCode.PARAM_ERROR, "开始日期不能大于结束日期");
        }
    }
    
    private void validateCreateSchedule(CreateScheduleDTO dto) {
        if (dto.getGroupId() == null || dto.getGroupId().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "分组ID不能为空");
        }
        if (dto.getStartDate() == null || dto.getStartDate().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "开始日期不能为空");
        }
        if (dto.getEndDate() == null || dto.getEndDate().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "结束日期不能为空");
        }
        validateDateRange(dto.getStartDate(), dto.getEndDate());
    }
    
    private Group validateAndGetGroup(String groupId) {
        LambdaQueryWrapper<Group> groupQueryWrapper = new LambdaQueryWrapper<>();
        groupQueryWrapper.eq(Group::getGroupId, groupId);
        Group group = groupService.getOne(groupQueryWrapper);
        
        if (group == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "分组不存在");
        }
        
        return group;
    }
}
