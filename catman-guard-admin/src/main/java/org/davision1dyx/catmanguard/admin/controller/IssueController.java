package org.davision1dyx.catmanguard.admin.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.service.IssueService;
import org.davision1dyx.catmanguard.api.admin.dto.AssignIssueDTO;
import org.davision1dyx.catmanguard.api.admin.dto.CreateIssueDTO;
import org.davision1dyx.catmanguard.api.admin.dto.IssueListDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateIssueDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateIssueStatusDTO;
import org.davision1dyx.catmanguard.api.admin.vo.CreateIssueVO;
import org.davision1dyx.catmanguard.api.admin.vo.IssueDetailVO;
import org.davision1dyx.catmanguard.api.admin.vo.IssueListVO;
import org.davision1dyx.catmanguard.api.admin.vo.OperationResultVO;
import org.davision1dyx.catmanguard.api.admin.vo.UpdateIssueVO;
import org.springframework.web.bind.annotation.*;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 工单中心接口
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/admin/issue")
public class IssueController {

    private final IssueService issueService;

    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    /**
     * 获取工单列表
     */
    @PostMapping("/list")
    public IssueListVO list(@RequestBody(required = false) IssueListDTO dto) {
        log.info("[POST] /processing/catman/admin/issue/list, params: {}", dto);
        if (dto == null) {
            dto = new IssueListDTO();
        }
        return issueService.listIssue(dto);
    }

    /**
     * 创建工单
     */
    @PostMapping("/create")
    public CreateIssueVO create(@RequestBody CreateIssueDTO dto) {
        log.info("[POST] /processing/catman/admin/issue/create, body: {}", dto);
        return issueService.createIssue(dto);
    }

    /**
     * 获取工单详情
     */
    @GetMapping("/{issueId}")
    public IssueDetailVO getDetail(@PathVariable String issueId) {
        log.info("[GET] /processing/catman/admin/issue/{}", issueId);
        return issueService.getIssueDetail(issueId);
    }

    /**
     * 更新工单
     */
    @PutMapping("/{issueId}")
    public UpdateIssueVO update(@PathVariable String issueId, @RequestBody UpdateIssueDTO dto) {
        log.info("[PUT] /processing/catman/admin/issue/{}", issueId);
        return issueService.updateIssue(issueId, dto);
    }

    /**
     * 删除工单
     */
    @DeleteMapping("/{issueId}")
    public OperationResultVO delete(@PathVariable String issueId) {
        log.info("[DELETE] /processing/catman/admin/issue/{}", issueId);
        boolean deleted = issueService.deleteIssue(issueId);
        return OperationResultVO.builder().success(deleted).build();
    }

    /**
     * 分配工单
     */
    @PostMapping("/assign/{issueId}")
    public OperationResultVO assign(@PathVariable String issueId, @RequestBody AssignIssueDTO dto) {
        log.info("[POST] /processing/catman/admin/issue/assign/{}", issueId);
        boolean assigned = issueService.assignIssue(issueId, dto);
        return OperationResultVO.builder().success(assigned).build();
    }

    /**
     * 更新工单状态
     */
    @PostMapping("/status/{issueId}")
    public OperationResultVO updateStatus(@PathVariable String issueId, @RequestBody UpdateIssueStatusDTO dto) {
        log.info("[POST] /processing/catman/admin/issue/status/{}", issueId);
        boolean updated = issueService.updateIssueStatus(issueId, dto.getStatus());
        return OperationResultVO.builder().success(updated).build();
    }

    /**
     * 添加回复
     */
    @PostMapping("/reply/{issueId}")
    public OperationResultVO reply(@PathVariable String issueId) {
        log.info("[POST] /processing/catman/admin/issue/reply/{}", issueId);
        // 实际实现需要添加回复逻辑
        return OperationResultVO.builder().success(true).build();
    }

    /**
     * 存储向量
     */
    @PostMapping("/store/{issueId}")
    public OperationResultVO storeVector(@PathVariable String issueId) {
        log.info("[POST] /processing/catman/admin/issue/store/{}", issueId);
        boolean updated = issueService.updateIssueStatus(issueId, "VECTOR_STORED");
        return OperationResultVO.builder().success(updated).build();
    }
}
