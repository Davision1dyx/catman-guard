package org.davision1dyx.catmanguard.admin.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.admin.mapper.IssueMapper;
import org.davision1dyx.catmanguard.admin.model.Issue;
import org.davision1dyx.catmanguard.admin.service.IssueService;
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

    @Override
    public IssueListVO listIssue(IssueListDTO dto) {
        log.info("Listing issues with search: {}, status: {}", dto.getSearch(), dto.getStatus());
        
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
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        IssueListVO result = new IssueListVO();
        result.setList(voList);
        result.setTotal((long) total);
        return result;
    }

    @Override
    public IssueDetailVO getIssueDetail(String issueId) {
        log.info("Getting issue detail: {}", issueId);
        
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Issue::getIssueId, issueId);
        Issue issue = getOne(queryWrapper);
        
        if (issue == null) {
            return null;
        }
        
        IssueDetailVO detailVO = new IssueDetailVO();
        detailVO.setId(issue.getIssueId());
        detailVO.setTitle(issue.getTitle());
        detailVO.setDescription(issue.getDescription());
        detailVO.setType(issue.getType());
        detailVO.setPriority(issue.getPriority());
        detailVO.setStatus(issue.getStatus());
        detailVO.setSubmitterId(issue.getSubmitterId());
        detailVO.setSubmitterName(issue.getSubmitterName());
        detailVO.setSubmitterEmail(issue.getSubmitterEmail());
        detailVO.setAssigneeId(issue.getAssigneeId());
        detailVO.setAssigneeName(issue.getAssigneeName());
        detailVO.setAssigneeEmail(issue.getAssigneeEmail());
        detailVO.setCreatedTime(issue.getCreateTime());
        detailVO.setUpdatedTime(issue.getUpdateTime());
        detailVO.setAttachments(new ArrayList<>());
        detailVO.setReplies(new ArrayList<>());
        
        return detailVO;
    }

    private IssueVO convertToVO(Issue issue) {
        IssueVO vo = new IssueVO();
        vo.setId(issue.getIssueId());
        vo.setTitle(issue.getTitle());
        vo.setDescription(issue.getDescription());
        vo.setType(issue.getType());
        vo.setPriority(issue.getPriority());
        vo.setStatus(issue.getStatus());
        vo.setSubmitterId(issue.getSubmitterId());
        vo.setSubmitterName(issue.getSubmitterName());
        vo.setSubmitterEmail(issue.getSubmitterEmail());
        vo.setAssigneeId(issue.getAssigneeId());
        vo.setAssigneeName(issue.getAssigneeName());
        vo.setAssigneeEmail(issue.getAssigneeEmail());
        vo.setCreatedTime(issue.getCreateTime());
        vo.setUpdatedTime(issue.getUpdateTime());
        return vo;
    }

    @Override
    public CreateIssueVO createIssue(CreateIssueDTO dto) {
        log.info("Creating issue: {}", dto.getTitle());
        
        Issue issue = new Issue();
        issue.setIssueId("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        issue.setTitle(dto.getTitle());
        issue.setDescription(dto.getDescription());
        issue.setType(dto.getType() != null ? dto.getType() : "INCIDENT");
        issue.setPriority(dto.getPriority() != null ? dto.getPriority() : "MEDIUM");
        issue.setStatus("ASSIGNED");
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
        
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Issue::getIssueId, issueId);
        Issue issue = getOne(queryWrapper);
        
        if (issue == null) {
            return null;
        }
        
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
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Issue::getIssueId, issueId);
        return remove(queryWrapper);
    }

    @Override
    public boolean assignIssue(String issueId, AssignIssueDTO dto) {
        log.info("Assigning issue {} to {}", issueId, dto.getAssigneeName());
        
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Issue::getIssueId, issueId);
        Issue issue = getOne(queryWrapper);
        
        if (issue == null) {
            return false;
        }
        
        issue.setAssigneeId(dto.getAssigneeId());
        issue.setAssigneeName(dto.getAssigneeName());
        issue.setAssigneeEmail(dto.getAssigneeEmail());
        issue.setStatus("ASSIGNED");
        updateById(issue);
        
        return true;
    }

    @Override
    public boolean updateIssueStatus(String issueId, String status) {
        log.info("Updating issue {} status to {}", issueId, status);
        
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Issue::getIssueId, issueId);
        Issue issue = getOne(queryWrapper);
        
        if (issue == null) {
            return false;
        }
        
        issue.setStatus(status);
        updateById(issue);
        
        return true;
    }
}
