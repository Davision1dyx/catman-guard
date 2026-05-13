package org.davision1dyx.catmanguard.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.convertor.StaffConvertor;
import org.davision1dyx.catmanguard.admin.enums.StaffStatus;
import org.davision1dyx.catmanguard.admin.mapper.StaffMapper;
import org.davision1dyx.catmanguard.admin.model.Group;
import org.davision1dyx.catmanguard.admin.model.Staff;
import org.davision1dyx.catmanguard.admin.service.GroupService;
import org.davision1dyx.catmanguard.admin.service.StaffService;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.api.admin.dto.CreateStaffDTO;
import org.davision1dyx.catmanguard.api.admin.dto.StaffListDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateStaffDTO;
import org.davision1dyx.catmanguard.api.admin.vo.CreateStaffVO;
import org.davision1dyx.catmanguard.api.admin.vo.StaffListVO;
import org.davision1dyx.catmanguard.api.admin.vo.StaffVO;
import org.davision1dyx.catmanguard.api.admin.vo.UpdateStaffVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 值班人员Service实现
 */
@Slf4j
@Service
public class StaffServiceImpl extends ServiceImpl<StaffMapper, Staff> implements StaffService {

    private final GroupService groupService;

    public StaffServiceImpl(GroupService groupService) {
        this.groupService = groupService;
    }

    @Override
    public StaffListVO listStaff(StaffListDTO dto) {
        log.info("Listing staff with search: {}", dto.getSearch());
        
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        if (dto.getSearch() != null && !dto.getSearch().isEmpty()) {
            queryWrapper.like(Staff::getName, dto.getSearch())
                       .or()
                       .like(Staff::getEmail, dto.getSearch());
        }
        
        IPage<Staff> page = new Page<>(1, Integer.MAX_VALUE);
        page = page(page, queryWrapper);
        
        List<StaffVO> staffVOList = page.getRecords().stream()
                .map(StaffConvertor.INSTANCE::mapToVO)
                .collect(Collectors.toList());
        
        StaffListVO result = new StaffListVO();
        result.setList(staffVOList);
        result.setTotal(page.getTotal());
        
        return result;
    }

    @Override
    public CreateStaffVO createStaff(CreateStaffDTO dto) {
        log.info("Creating staff: {}", dto.getName());
        
        validateCreateStaff(dto);
        
        Staff staff = new Staff();
        if (dto.getId() == null || dto.getId().isEmpty()) {
            staff.setStaffId("staff-" + UUID.randomUUID().toString().substring(0, 8));
        } else {
            staff.setStaffId(dto.getId());
        }
        staff.setName(dto.getName());
        staff.setEmail(dto.getEmail());
        staff.setPhone(dto.getPhone());
        staff.setGroupId(dto.getGroupId());
        if (dto.getGroupId() != null) {
            Group group = validateAndGetGroup(dto.getGroupId());
            staff.setGroupName(group.getName());
        }
        
        staff.setStatus(StaffStatus.ACTIVE.name());
        save(staff);
        
        CreateStaffVO result = new CreateStaffVO();
        result.setStaffId(staff.getStaffId());
        return result;
    }

    @Override
    public UpdateStaffVO updateStaff(String staffId, UpdateStaffDTO dto) {
        log.info("Updating staff: {}", staffId);
        
        Staff staff = validateAndGetStaff(staffId);
        validateUpdateStaff(dto);
        
        if (dto.getName() != null) {
            staff.setName(dto.getName());
        }
        if (dto.getEmail() != null) {
            staff.setEmail(dto.getEmail());
        }
        if (dto.getPhone() != null) {
            staff.setPhone(dto.getPhone());
        }
        if (dto.getGroupId() != null) {
            Group group = validateAndGetGroup(dto.getGroupId());
            staff.setGroupId(dto.getGroupId());
            staff.setGroupName(group.getName());
        }
        if (dto.getStatus() != null) {
            staff.setStatus(dto.getStatus());
        }
        
        updateById(staff);
        
        UpdateStaffVO result = new UpdateStaffVO();
        result.setStaffId(staff.getStaffId());
        return result;
    }

    @Override
    public boolean deleteStaff(String staffId) {
        log.info("Deleting staff: {}", staffId);
        
        validateStaffExists(staffId);
        
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Staff::getStaffId, staffId);
        return remove(queryWrapper);
    }
    
    private boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
    
    private boolean isValidStaffStatus(String status) {
        for (StaffStatus staffStatus : StaffStatus.values()) {
            if (staffStatus.name().equals(status)) {
                return true;
            }
        }
        return false;
    }
    
    private void validateCreateStaff(CreateStaffDTO dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "人员姓名不能为空");
        }
        if (dto.getEmail() == null || dto.getEmail().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "人员邮箱不能为空");
        }
        if (!isValidEmail(dto.getEmail())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "邮箱格式不合法");
        }
    }
    
    private void validateUpdateStaff(UpdateStaffDTO dto) {
        if (dto.getName() != null && dto.getName().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "人员姓名不能为空");
        }
        if (dto.getEmail() != null && !dto.getEmail().isEmpty() && !isValidEmail(dto.getEmail())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "邮箱格式不合法");
        }
        if (dto.getStatus() != null && !isValidStaffStatus(dto.getStatus())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "人员状态不合法");
        }
    }
    
    private Staff validateAndGetStaff(String staffId) {
        if (staffId == null || staffId.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "人员ID不能为空");
        }
        
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Staff::getStaffId, staffId);
        Staff staff = getOne(queryWrapper);
        
        if (staff == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "人员不存在");
        }
        
        return staff;
    }
    
    private void validateStaffExists(String staffId) {
        validateAndGetStaff(staffId);
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
