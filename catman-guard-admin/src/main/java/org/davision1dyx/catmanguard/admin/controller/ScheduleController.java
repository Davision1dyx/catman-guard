package org.davision1dyx.catmanguard.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.service.ScheduleService;
import org.davision1dyx.catmanguard.api.admin.dto.CreateScheduleDTO;
import org.davision1dyx.catmanguard.api.admin.dto.ScheduleListDTO;
import org.davision1dyx.catmanguard.api.admin.vo.CreateScheduleVO;
import org.davision1dyx.catmanguard.api.admin.vo.ScheduleListVO;
import org.springframework.web.bind.annotation.*;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 排班管理接口
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/admin/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    /**
     * 获取排班列表
     */
    @GetMapping("/list")
    public ScheduleListVO list(@RequestParam(required = false) String startDate,
                               @RequestParam(required = false) String endDate) {
        log.info("[GET] /processing/catman/admin/schedule/list, startDate: {}, endDate: {}", startDate, endDate);
        ScheduleListDTO dto = new ScheduleListDTO();
        dto.setStartDate(startDate);
        dto.setEndDate(endDate);
        return scheduleService.listSchedule(dto);
    }

    /**
     * 创建排班
     */
    @PostMapping("/create")
    public CreateScheduleVO create(@RequestBody CreateScheduleDTO dto) {
        log.info("[POST] /processing/catman/admin/schedule/create, body: {}", dto);
        return scheduleService.createSchedule(dto);
    }
}