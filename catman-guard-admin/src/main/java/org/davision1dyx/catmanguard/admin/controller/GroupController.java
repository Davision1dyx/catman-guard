package org.davision1dyx.catmanguard.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.service.GroupService;
import org.davision1dyx.catmanguard.api.admin.dto.CreateGroupDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateGroupDTO;
import org.davision1dyx.catmanguard.api.admin.vo.GroupVO;
import org.davision1dyx.catmanguard.api.admin.vo.OperationResultVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 分组管理接口
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/admin/group")
public class GroupController {

    private final GroupService groupService;

    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * 获取分组列表
     */
    @GetMapping("/list")
    public List<GroupVO> list() {
        log.info("[GET] /processing/catman/admin/group/list");
        return groupService.listGroupWithMembers();
    }

    /**
     * 创建分组
     */
    @PostMapping("/create")
    public GroupVO create(@RequestBody CreateGroupDTO dto) {
        log.info("[POST] /processing/catman/admin/group/create, body: {}", dto);
        return groupService.createGroup(dto);
    }

    /**
     * 更新分组
     */
    @PutMapping("/modify/{id}")
    public GroupVO update(@PathVariable String id, @RequestBody UpdateGroupDTO dto) {
        log.info("[PUT] /processing/catman/admin/group/modify/{}", id);
        return groupService.updateGroup(id, dto);
    }

    /**
     * 删除分组
     */
    @DeleteMapping("/delete/{id}")
    public OperationResultVO delete(@PathVariable String id) {
        log.info("[DELETE] /processing/catman/admin/group/delete/{}", id);
        boolean deleted = groupService.deleteGroup(id);
        return OperationResultVO.builder().success(deleted).build();
    }
}
