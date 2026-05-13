package org.davision1dyx.catmanguard.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.convertor.IssueConvertor;
import org.davision1dyx.catmanguard.admin.enums.IssueStatus;
import org.davision1dyx.catmanguard.admin.enums.IssueType;
import org.davision1dyx.catmanguard.admin.enums.IssuePriority;
import org.davision1dyx.catmanguard.admin.mapper.IssueMapper;
import org.davision1dyx.catmanguard.admin.model.Issue;
import org.davision1dyx.catmanguard.admin.model.Staff;
import org.davision1dyx.catmanguard.admin.service.IssueService;
import org.davision1dyx.catmanguard.admin.service.StaffService;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.api.admin.dto.AssignIssueDTO;
import org.davision1dyx.catmanguard.api.admin.dto.CreateIssueDTO;
import org.davision1dyx.catmanguard.api.admin.dto.IssueListDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateIssueDTO;
import org.davision1dyx.catmanguard.api.admin.vo.CreateIssueVO;
import org.davision1dyx.catmanguard.api.admin.vo.IssueDetailVO;
import org.davision1dyx.catmanguard.api.admin.vo.IssueListVO;
import org.davision1dyx.catmanguard.api.admin.vo.IssueVO;
import org.davision1dyx.catmanguard.api.admin.vo.UpdateIssueVO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 工单Service实现
 */
@Slf4j
@Service
public class IssueServiceImpl extends ServiceImpl<IssueMapper, Issue> implements IssueService {

    private final StaffService staffService;

    public IssueServiceImpl(StaffService staffService) {
        this.staffService = staffService;
    }

    @Override
    public IssueListVO listIssue(IssueListDTO dto) {
        log.info("Listing issues with search: {}, status: {}", dto.getSearch(), dto.getStatus());
        
        if (dto.getPage() != null && dto.getPage() < 1) {
            throw new BizException(ErrorCode.PARAM_ERROR, "页码必须大于0");
        }
        if (dto.getSize() != null && (dto.getSize() < 1 || dto.getSize() > 100)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "每页大小必须在1-100之间");
        }
        
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        if (dto.getSearch() != null && !dto.getSearch().isEmpty()) {
            queryWrapper.like(Issue::getTitle, dto.getSearch()).or().like(Issue::getDescription, dto.getSearch());
        }
        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            queryWrapper.eq(Issue::getStatus, dto.getStatus());
        }
        if (dto.getPriority() != null && !dto.getPriority().isEmpty()) {
            queryWrapper.eq(Issue::getPriority, dto.getPriority());
        }
        if (dto.getAssignee() != null && !dto.getAssignee().isEmpty()) {
            queryWrapper.like(Issue::getAssigneeName, dto.getAssignee());
        }
        if (dto.getType() != null && !dto.getType().isEmpty()) {
            queryWrapper.eq(Issue::getType, dto.getType());
        }
        
        List<Issue> issueList = list(queryWrapper);
        
        int total = issueList.size();
        int page = dto.getPage() != null ? dto.getPage() : 1;
        int size = dto.getSize() != null ? dto.getSize() : 10;
        int fromIndex = (page - 1) * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<Issue> pageList = fromIndex < total ? issueList.subList(fromIndex, toIndex) : new ArrayList<>();
        
        List<IssueVO> voList = pageList.stream()
                .map(IssueConvertor.INSTANCE::mapToVO)
                .collect(Collectors.toList());
        
        IssueListVO result = new IssueListVO();
        result.setList(voList);
        result.setTotal((long) total);
        return result;
    }

    @Override
    public IssueDetailVO getIssueDetail(String issueId) {
        log.info("Getting issue detail: {}", issueId);

        if (issueId == null || issueId.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单ID不能为空");
        }

        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Issue::getIssueId, issueId);
        Issue issue = getOne(queryWrapper);

        if (issue == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单不存在");
        }

        return IssueConvertor.INSTANCE.mapToDetailVO(issue);
    }

    @Override
    public CreateIssueVO createIssue(CreateIssueDTO dto) {
        log.info("Creating issue: {}", dto.getTitle());
        
        validateCreateIssue(dto);
        
        Issue issue = new Issue();
        issue.setIssueId("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());
        issue.setType(dto.getType() != null ? dto.getType() : IssueType.INCIDENT.name());
        issue.setPriority(dto.getPriority() != null ? dto.getPriority() : IssuePriority.MEDIUM.name());
        issue.setStatus(IssueStatus.ASSIGNED.name());
        issue.setSubmitterId(dto.getSubmitterId());
        issue.setSubmitterName(dto.getSubmitterName());
        issue.setSubmitterEmail(dto.getSubmitterEmail());
        
        save(issue);
        
        CreateIssueVO result = new CreateIssueVO();
        result.setIssueId(issue.getIssueId());
        return result;
    }

    @Override
    public UpdateIssueVO updateIssue(String issueId, UpdateIssueDTO dto) {
        log.info("Updating issue: {}", issueId);
        
        Issue issue = validateAndGetIssue(issueId);
        validateUpdateIssue(dto);
        
        if (dto.getTitle() != null) {
            issue.setTitle(dto.getTitle());
        }
        if (dto.getDescription() != null) {
            issue.setDescription(dto.getDescription());
        }
        if (dto.getType() != null) {
            issue.setType(dto.getType());
        }
        if (dto.getPriority() != null) {
            issue.setPriority(dto.getPriority());
        }
        
        updateById(issue);
        
        UpdateIssueVO result = new UpdateIssueVO();
        result.setIssueId(issue.getIssueId());
        return result;
    }

    @Override
    public boolean deleteIssue(String issueId) {
        log.info("Deleting issue: {}", issueId);
        
        validateIssueExists(issueId);
        
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Issue::getIssueId, issueId);
        return remove(queryWrapper);
    }

    @Override
    public boolean assignIssue(String issueId, AssignIssueDTO dto) {
        log.info("Assigning issue {} to {}", issueId, dto.getAssigneeName());

        Issue issue = validateAndGetIssue(issueId);
        Staff staff = validateAndGetStaff(dto.getAssigneeId());

        issue.setAssigneeId(staff.getStaffId());
        issue.setAssigneeName(staff.getName());
        issue.setAssigneeEmail(staff.getEmail());
        issue.setStatus(IssueStatus.ASSIGNED.name());
        updateById(issue);

        return true;
    }

    @Override
    public boolean updateIssueStatus(String issueId, String status) {
        log.info("Updating issue {} status to {}", issueId, status);
        
        if (issueId == null || issueId.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单ID不能为空");
        }
        if (status == null || status.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单状态不能为空");
        }
        if (!isValidIssueStatus(status)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单状态不合法");
        }
        
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Issue::getIssueId, issueId);
        Issue issue = getOne(queryWrapper);
        
        if (issue == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单不存在");
        }
        
        issue.setStatus(status);
        updateById(issue);
        
        return true;
    }
    
    private boolean isValidIssueType(String type) {
        for (IssueType issueType : IssueType.values()) {
            if (issueType.name().equals(type)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidIssuePriority(String priority) {
        for (IssuePriority issuePriority : IssuePriority.values()) {
            if (issuePriority.name().equals(priority)) {
                return true;
            }
        }
        return false;
    }
    
    private boolean isValidIssueStatus(String status) {
        for (IssueStatus issueStatus : IssueStatus.values()) {
            if (issueStatus.name().equals(status)) {
                return true;
            }
        }
        return false;
    }
    
    private void validateCreateIssue(CreateIssueDTO dto) {
        if (dto.getTitle() == null || dto.getTitle().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单标题不能为空");
        }
        if (dto.getSubmitterId() == null || dto.getSubmitterId().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "提交人ID不能为空");
        }
        if (dto.getSubmitterName() == null || dto.getSubmitterName().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "提交人姓名不能为空");
        }
        if (dto.getType() != null && !isValidIssueType(dto.getType())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单类型不合法");
        }
        if (dto.getPriority() != null && !isValidIssuePriority(dto.getPriority())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单优先级不合法");
        }
    }
    
    private void validateUpdateIssue(UpdateIssueDTO dto) {
        if (dto.getTitle() != null && dto.getTitle().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单标题不能为空");
        }
        if (dto.getType() != null && !isValidIssueType(dto.getType())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单类型不合法");
        }
        if (dto.getPriority() != null && !isValidIssuePriority(dto.getPriority())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单优先级不合法");
        }
    }
    
    private Issue validateAndGetIssue(String issueId) {
        if (issueId == null || issueId.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单ID不能为空");
        }
        
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Issue::getIssueId, issueId);
        Issue issue = getOne(queryWrapper);
        
        if (issue == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单不存在");
        }
        
        return issue;
    }
    
    private void validateIssueExists(String issueId) {
        validateAndGetIssue(issueId);
    }
    
    private Staff validateAndGetStaff(String staffId) {
        if (staffId == null || staffId.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "处理人ID不能为空");
        }
        
        LambdaQueryWrapper<Staff> staffQueryWrapper = new LambdaQueryWrapper<>();
        staffQueryWrapper.eq(Staff::getStaffId, staffId);
        Staff staff = staffService.getOne(staffQueryWrapper);
        
        if (staff == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "处理人不存在");
        }
        
        return staff;
    }
    
    private void validateIssueStatus(String status) {
        if (status == null || status.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单状态不能为空");
        }
        if (!isValidIssueStatus(status)) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单状态不合法");
        }
    }
}
