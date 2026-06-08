package org.davision1dyx.catmanguard.admin.service.impl;

import com.alibaba.fastjson2.JSON;
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
import org.davision1dyx.catmanguard.api.admin.service.IssueAIService;
import org.davision1dyx.catmanguard.api.admin.vo.*;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.api.admin.dto.AssignIssueDTO;
import org.davision1dyx.catmanguard.api.admin.dto.CreateIssueDTO;
import org.davision1dyx.catmanguard.api.admin.dto.IssueListDTO;
import org.davision1dyx.catmanguard.api.admin.dto.LinkIssueKnowledgeDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateIssueDTO;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkEmbedDTO;
import org.davision1dyx.catmanguard.api.storage.dto.IssueFileCreateDTO;
import org.davision1dyx.catmanguard.api.storage.service.ChunkService;
import org.davision1dyx.catmanguard.api.storage.service.IssueFileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private static final String DEFAULT_KNOWLEDGE_ID = "kb-operation-tickets";

    private final StaffService staffService;
    private final ChunkService chunkService;
    private final IssueFileService issueFileService;
    private final IssueAIService issueAIService;

    public IssueServiceImpl(StaffService staffService, ChunkService chunkService,
                            IssueFileService issueFileService,
                            IssueAIService issueAIService) {
        this.staffService = staffService;
        this.chunkService = chunkService;
        this.issueFileService = issueFileService;
        this.issueAIService = issueAIService;
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
        issue.setKnowledgeId(dto.getKnowledgeId() != null ? dto.getKnowledgeId() : DEFAULT_KNOWLEDGE_ID);

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
        
        // 当工单状态变为COMPLETED时，自动触发向量化
        if (IssueStatus.COMPLETED.name().equals(status)) {
            try {
                vectorizeIssue(issueId);
                log.info("Auto vectorized issue: {} after status changed to COMPLETED", issueId);
            } catch (Exception e) {
                log.warn("Auto vectorization failed for issue {}: {}", issueId, e.getMessage());
            }
        }
        
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

    @Override
    public boolean linkKnowledge(String issueId, LinkIssueKnowledgeDTO dto) {
        log.info("Linking issue {} to knowledge base {}", issueId, dto.getKnowledgeId());

        if (issueId == null || issueId.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单ID不能为空");
        }
        if (dto.getKnowledgeId() == null || dto.getKnowledgeId().isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "知识库ID不能为空");
        }

        Issue issue = validateAndGetIssue(issueId);
        issue.setKnowledgeId(dto.getKnowledgeId());
        updateById(issue);

        log.info("Successfully linked issue {} to knowledge base {}", issueId, dto.getKnowledgeId());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public IssueVectorizeVO vectorizeIssue(String issueId) {
        log.info("Vectorizing issue: {}", issueId);

        if (issueId == null || issueId.isEmpty()) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单ID不能为空");
        }

        Issue issue = validateAndGetIssue(issueId);

        if (!IssueStatus.COMPLETED.name().equals(issue.getStatus())) {
            throw new BizException(ErrorCode.PARAM_ERROR, "工单状态必须是COMPLETED才能向量化");
        }

        String knowledgeId = issue.getKnowledgeId();
        if (knowledgeId == null || knowledgeId.isEmpty()) {
            knowledgeId = DEFAULT_KNOWLEDGE_ID;
        }

        try {
            String fileId = "file-issue-" + issue.getIssueId().toLowerCase();

            // 检查文件是否已存在
            if (issueFileService.existsFile(fileId)) {
                log.info("Issue {} already vectorized, fileId: {}", issueId, fileId);
                return IssueVectorizeVO.builder()
                        .chunkSize(0)
                        .fileId(fileId)
                        .build();
            }

            String issueContent = buildIssueContent(issue);
            
            // 生成摘要
            String summary = issueAIService.generateSummary(issueContent);
            issue.setSummary(summary);
            
            // 进行问题归因
            String rootCause = issueAIService.analyzeRootCause(issueContent);
            issue.setRootCause(rootCause);

            // 创建工单虚拟文件
            IssueFileCreateDTO fileCreateDTO = IssueFileCreateDTO.builder()
                    .fileId(fileId)
                    .fileName("工单-" + issue.getIssueId())
                    .fileTitle(issue.getTitle())
                    .knowledgeId(knowledgeId)
                    .description(buildIssueDescription(issue))
                    .build();
            issueFileService.createIssueFile(fileCreateDTO);

            // 创建文件分片
            String content = buildChunkContentWithAI(issue);
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("issueId", issue.getIssueId());
            metadata.put("type", issue.getType());
            metadata.put("priority", issue.getPriority());
            metadata.put("submitterName", issue.getSubmitterName());
            metadata.put("assigneeName", issue.getAssigneeName());

            issueFileService.createIssueChunk(fileId, content, JSON.toJSONString(metadata));

            // 触发向量化
            ChunkEmbedDTO embedDTO = new ChunkEmbedDTO();
            embedDTO.setFileId(fileId);
            chunkService.embed(embedDTO);

            // 更新工单状态
            issue.setStatus(IssueStatus.VECTOR_STORED.name());
            updateById(issue);

            log.info("Successfully vectorized issue {}, created fileId: {}", issueId, fileId);
            return IssueVectorizeVO.builder()
                    .chunkSize(1)
                    .fileId(fileId)
                    .build();

        } catch (Exception e) {
            log.error("Failed to vectorize issue: " + issueId, e);
            throw new BizException(ErrorCode.ERROR, "向量化失败: " + e.getMessage());
        }
    }

    private String buildIssueDescription(Issue issue) {
        StringBuilder sb = new StringBuilder();
        sb.append("工单类型: ").append(issue.getType() != null ? issue.getType() : "INCIDENT").append("\n");
        sb.append("优先级: ").append(issue.getPriority() != null ? issue.getPriority() : "MEDIUM").append("\n");
        sb.append("提交人: ").append(issue.getSubmitterName() != null ? issue.getSubmitterName() : "未知").append("\n");
        sb.append("处理人: ").append(issue.getAssigneeName() != null ? issue.getAssigneeName() : "未分配").append("\n");
        sb.append("问题描述: ").append(issue.getDescription() != null ? issue.getDescription() : "无");
        return sb.toString();
    }

    private String buildChunkContent(Issue issue) {
        StringBuilder sb = new StringBuilder();
        sb.append("【工单标题】").append(issue.getTitle()).append("\n\n");
        sb.append("【工单类型】").append(issue.getType() != null ? issue.getType() : "INCIDENT").append("\n");
        sb.append("【优先级】").append(issue.getPriority() != null ? issue.getPriority() : "MEDIUM").append("\n");
        sb.append("【提交人】").append(issue.getSubmitterName() != null ? issue.getSubmitterName() : "未知");
        sb.append(" (").append(issue.getSubmitterEmail() != null ? issue.getSubmitterEmail() : "无邮箱").append(")\n");
        sb.append("【处理人】").append(issue.getAssigneeName() != null ? issue.getAssigneeName() : "未分配");
        sb.append(" (").append(issue.getAssigneeEmail() != null ? issue.getAssigneeEmail() : "无邮箱").append(")\n\n");
        sb.append("【问题描述】\n").append(issue.getDescription() != null ? issue.getDescription() : "无");
        return sb.toString();
    }

    /**
     * 构建用于AI分析的工单内容
     */
    private String buildIssueContent(Issue issue) {
        StringBuilder sb = new StringBuilder();
        sb.append("工单编号: ").append(issue.getIssueId()).append("\n");
        sb.append("工单标题: ").append(issue.getTitle()).append("\n");
        sb.append("工单类型: ").append(issue.getType() != null ? issue.getType() : "INCIDENT").append("\n");
        sb.append("优先级: ").append(issue.getPriority() != null ? issue.getPriority() : "MEDIUM").append("\n");
        sb.append("状态: ").append(issue.getStatus() != null ? issue.getStatus() : "ASSIGNED").append("\n");
        sb.append("提交人: ").append(issue.getSubmitterName() != null ? issue.getSubmitterName() : "未知").append("\n");
        sb.append("处理人: ").append(issue.getAssigneeName() != null ? issue.getAssigneeName() : "未分配").append("\n");
        sb.append("问题描述: ").append(issue.getDescription() != null ? issue.getDescription() : "无");
        return sb.toString();
    }

    /**
     * 构建包含AI分析结果的chunk内容
     */
    private String buildChunkContentWithAI(Issue issue) {
        StringBuilder sb = new StringBuilder();
        sb.append("【工单标题】").append(issue.getTitle()).append("\n\n");
        sb.append("【工单类型】").append(issue.getType() != null ? issue.getType() : "INCIDENT").append("\n");
        sb.append("【优先级】").append(issue.getPriority() != null ? issue.getPriority() : "MEDIUM").append("\n");
        sb.append("【提交人】").append(issue.getSubmitterName() != null ? issue.getSubmitterName() : "未知");
        sb.append(" (").append(issue.getSubmitterEmail() != null ? issue.getSubmitterEmail() : "无邮箱").append(")\n");
        sb.append("【处理人】").append(issue.getAssigneeName() != null ? issue.getAssigneeName() : "未分配");
        sb.append(" (").append(issue.getAssigneeEmail() != null ? issue.getAssigneeEmail() : "无邮箱").append(")\n\n");
        sb.append("【问题描述】\n").append(issue.getDescription() != null ? issue.getDescription() : "无").append("\n\n");
        
        // 添加AI生成的摘要
        if (issue.getSummary() != null && !issue.getSummary().isEmpty()) {
            sb.append("【工单摘要】\n").append(issue.getSummary()).append("\n\n");
        }
        
        // 添加问题归因分析
        if (issue.getRootCause() != null && !issue.getRootCause().isEmpty()) {
            sb.append("【问题归因】\n").append(issue.getRootCause()).append("\n");
        }
        
        return sb.toString();
    }

    @Override
    public HotIssueListVO identifyHotIssues(Integer topN) {
        log.info("Identifying hot issues, topN: {}", topN);
        
        int limit = topN != null && topN > 0 ? topN : 10;
        
        // 查询最近一段时间内的工单（这里简单实现，按标题关键词统计）
        LambdaQueryWrapper<Issue> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.ne(Issue::getStatus, IssueStatus.VECTOR_STORED.name());
        List<Issue> recentIssues = list(queryWrapper);
        
        // 简单的关键词统计（实际应用中可以使用更复杂的NLP分析）
        Map<String, List<Issue>> keywordMap = new HashMap<>();
        
        // 常见运维问题关键词
        String[] commonKeywords = {
            "服务器", "数据库", "网络", "连接", "超时", "错误", "异常", "崩溃", 
            "性能", "内存", "CPU", "磁盘", "重启", "故障", "宕机", "延迟"
        };
        
        for (Issue issue : recentIssues) {
            String title = issue.getTitle() != null ? issue.getTitle() : "";
            String desc = issue.getDescription() != null ? issue.getDescription() : "";
            String content = title + " " + desc;
            
            for (String keyword : commonKeywords) {
                if (content.contains(keyword)) {
                    keywordMap.computeIfAbsent(keyword, k -> new ArrayList<>()).add(issue);
                }
            }
        }
        
        // 按数量排序并取topN
        List<HotIssueVO> hotIssueList = keywordMap.entrySet().stream()
                .map(entry -> {
                    String keyword = entry.getKey();
                    List<Issue> issues = entry.getValue();
                    int count = issues.size();
                    
                    // 确定热点级别
                    String level;
                    if (count >= 10) {
                        level = "HIGH";
                    } else if (count >= 5) {
                        level = "MEDIUM";
                    } else {
                        level = "LOW";
                    }
                    
                    // 简单趋势判断（实际应用中可以比较历史数据）
                    String trend = "STABLE";
                    
                    // 转换为VO
                    List<IssueVO> issueVOList = issues.stream()
                            .limit(5) // 每个热点最多返回5个工单
                            .map(IssueConvertor.INSTANCE::mapToVO)
                            .collect(Collectors.toList());
                    
                    return HotIssueVO.builder()
                            .keyword(keyword)
                            .count(count)
                            .level(level)
                            .trend(trend)
                            .issues(issueVOList)
                            .build();
                })
                .sorted((a, b) -> b.getCount().compareTo(a.getCount()))
                .limit(limit)
                .collect(Collectors.toList());
        
        return HotIssueListVO.builder()
                .list(hotIssueList)
                .total(hotIssueList.size())
                .build();
    }
}
