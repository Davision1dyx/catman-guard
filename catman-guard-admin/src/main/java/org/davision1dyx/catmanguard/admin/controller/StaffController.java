package org.davision1dyx.catmanguard.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.service.GroupService;
import org.davision1dyx.catmanguard.admin.service.StaffService;
import org.davision1dyx.catmanguard.api.admin.dto.CreateStaffDTO;
import org.davision1dyx.catmanguard.api.admin.dto.StaffListDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateStaffDTO;
import org.davision1dyx.catmanguard.api.admin.vo.CreateStaffVO;
import org.davision1dyx.catmanguard.api.admin.vo.StaffListVO;
import org.davision1dyx.catmanguard.api.admin.vo.UpdateStaffVO;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 值班人员管理接口
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/admin/staff")
public class StaffController {

    private final StaffService staffService;
    private final GroupService groupService;

    public StaffController(StaffService staffService, GroupService groupService) {
        this.staffService = staffService;
        this.groupService = groupService;
    }

    /**
     * 获取值班人员列表
     */
    @GetMapping("/list")
    public StaffListVO list(@RequestParam(required = false) String search) {
        log.info("[GET] /processing/catman/admin/staff/list, search: {}", search);
        
        StaffListDTO dto = new StaffListDTO();
        dto.setSearch(search);
        
        return staffService.listStaff(dto);
    }

    /**
     * 创建值班人员
     */
    @PostMapping("/create")
    public CreateStaffVO create(@RequestBody CreateStaffDTO dto) {
        log.info("[POST] /processing/catman/admin/staff/create, body: {}", dto);
        return staffService.createStaff(dto);
    }

    /**
     * 更新值班人员
     */
    @PutMapping("/modify/{id}")
    public UpdateStaffVO update(@PathVariable String id, @RequestBody UpdateStaffDTO dto) {
        log.info("[PUT] /processing/catman/admin/staff/modify/{}", id);
        return staffService.updateStaff(id, dto);
    }

    /**
     * 删除值班人员
     */
    @DeleteMapping("/delete/{id}")
    public Boolean delete(@PathVariable String id) {
        log.info("[DELETE] /processing/catman/admin/staff/delete/{}", id);
        return staffService.deleteStaff(id);
    }
}
