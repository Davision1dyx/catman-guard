package org.davision1dyx.catmanguard.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.convertor.StaffConvertor;
import org.davision1dyx.catmanguard.admin.mapper.StaffMapper;
import org.davision1dyx.catmanguard.admin.model.Staff;
import org.davision1dyx.catmanguard.admin.service.StaffService;
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
            staff.setGroupName("分组" + dto.getGroupId().substring(dto.getGroupId().length() - 1));
        }
        
        staff.setStatus("ACTIVE");
        save(staff);
        
        CreateStaffVO result = new CreateStaffVO();
        result.setStaffId(staff.getStaffId());
        return result;
    }

    @Override
    public UpdateStaffVO updateStaff(String staffId, UpdateStaffDTO dto) {
        log.info("Updating staff: {}", staffId);
        
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Staff::getStaffId, staffId);
        Staff staff = getOne(queryWrapper);
        
        if (staff == null) {
            return null;
        }
        
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
            staff.setGroupId(dto.getGroupId());
            staff.setGroupName("分组" + dto.getGroupId().substring(dto.getGroupId().length() - 1));
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
        LambdaQueryWrapper<Staff> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Staff::getStaffId, staffId);
        return remove(queryWrapper);
    }
}
