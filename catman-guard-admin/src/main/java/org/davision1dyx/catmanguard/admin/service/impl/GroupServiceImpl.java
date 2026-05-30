package org.davision1dyx.catmanguard.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.mapper.GroupMapper;
import org.davision1dyx.catmanguard.admin.model.Group;
import org.davision1dyx.catmanguard.admin.model.Staff;
import org.davision1dyx.catmanguard.admin.service.GroupService;
import org.davision1dyx.catmanguard.admin.service.StaffService;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.api.admin.dto.CreateGroupDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateGroupDTO;
import org.davision1dyx.catmanguard.api.admin.vo.GroupVO;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 分组Service实现
 */
@Slf4j
@Service
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {

    private final StaffService staffService;

    public GroupServiceImpl(@Lazy StaffService staffService) {
        this.staffService = staffService;
    }

    @Override
    public List<GroupVO> listGroupWithMembers() {
        log.info("Listing groups with members");
        
        List<Group> groupList = list();
        
        return groupList.stream().map(group -> {
            GroupVO vo = new GroupVO();
            vo.setId(group.getGroupId());
            vo.setName(group.getName());
            
            LambdaQueryWrapper<Staff> staffQueryWrapper = new LambdaQueryWrapper<>();
            staffQueryWrapper.eq(Staff::getGroupId, group.getGroupId());
            List<Staff> members = staffService.list(staffQueryWrapper);
            
            List<GroupVO.GroupMemberVO> memberVOList = members.stream()
                    .map(m -> {
                        GroupVO.GroupMemberVO memberVO = new GroupVO.GroupMemberVO();
                        memberVO.setId(m.getStaffId());
                        memberVO.setName(m.getName());
                        return memberVO;
                    })
                    .collect(Collectors.toList());
            vo.setMembers(memberVOList);
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public GroupVO createGroup(CreateGroupDTO dto) {
        log.info("Creating group: {}", dto.getName());
        
        validateCreateGroup(dto);
        
        Group group = new Group();
        group.setGroupId("group-" + UUID.randomUUID().toString().substring(0, 8));
        group.setName(dto.getName());
        save(group);
        
        GroupVO vo = new GroupVO();
        vo.setId(group.getGroupId());
        vo.setName(group.getName());
        vo.setMembers(List.of());
        
        return vo;
    }

    @Override
    public GroupVO updateGroup(String groupId, UpdateGroupDTO dto) {
        log.info("Updating group: {}", groupId);
        
        Group group = validateAndGetGroup(groupId);
        validateUpdateGroup(dto);
        
        if (dto.getName() != null) {
            group.setName(dto.getName());
        }
        updateById(group);
        
        return listGroupWithMembers().stream()
                .filter(g -> g.getId().equals(groupId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean deleteGroup(String groupId) {
        log.info("Deleting group: {}", groupId);
        
        Group group = validateAndGetGroup(groupId);
        validateGroupNotInUse(groupId);
        
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getGroupId, groupId);
        return remove(queryWrapper);
    }
    
    private void validateCreateGroup(CreateGroupDTO dto) {
        if (dto.getName() == null || dto.getName().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "分组名称不能为空");
        }
    }
    
    private void validateUpdateGroup(UpdateGroupDTO dto) {
        if (dto.getName() != null && dto.getName().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "分组名称不能为空");
        }
    }
    
    private Group validateAndGetGroup(String groupId) {
        if (groupId == null || groupId.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "分组ID不能为空");
        }
        
        LambdaQueryWrapper<Group> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Group::getGroupId, groupId);
        Group group = getOne(queryWrapper);
        
        if (group == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "分组不存在");
        }
        
        return group;
    }
    
    private void validateGroupNotInUse(String groupId) {
        LambdaQueryWrapper<Staff> staffQueryWrapper = new LambdaQueryWrapper<>();
        staffQueryWrapper.eq(Staff::getGroupId, groupId);
        List<Staff> staffList = staffService.list(staffQueryWrapper);
        
        if (!staffList.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "分组下存在人员，无法删除");
        }
    }
}
