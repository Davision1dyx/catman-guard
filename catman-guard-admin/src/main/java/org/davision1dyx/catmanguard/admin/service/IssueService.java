package org.davision1dyx.catmanguard.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.davision1dyx.catmanguard.admin.model.Issue;
import org.davision1dyx.catmanguard.api.admin.dto.AssignIssueDTO;
import org.davision1dyx.catmanguard.api.admin.dto.CreateIssueDTO;
import org.davision1dyx.catmanguard.api.admin.dto.IssueListDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateIssueDTO;
import org.davision1dyx.catmanguard.api.admin.vo.CreateIssueVO;
import org.davision1dyx.catmanguard.api.admin.vo.IssueDetailVO;
import org.davision1dyx.catmanguard.api.admin.vo.IssueListVO;
import org.davision1dyx.catmanguard.api.admin.vo.UpdateIssueVO;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 工单Service接口
 */
public interface IssueService extends IService<Issue> {

    /**
     * 获取工单列表
     * @param dto 查询条件
     * @return 工单列表
     */
    IssueListVO listIssue(IssueListDTO dto);

    /**
     * 获取工单详情
     * @param issueId 工单ID
     * @return 工单详情
     */
    IssueDetailVO getIssueDetail(String issueId);

    /**
     * 创建工单
     * @param dto 创建参数
     * @return 创建结果
     */
    CreateIssueVO createIssue(CreateIssueDTO dto);

    /**
     * 更新工单
     * @param issueId 工单ID
     * @param dto 更新参数
     * @return 更新结果
     */
    UpdateIssueVO updateIssue(String issueId, UpdateIssueDTO dto);

    /**
     * 删除工单
     * @param issueId 工单ID
     * @return 是否删除成功
     */
    boolean deleteIssue(String issueId);

    /**
     * 分配工单
     * @param issueId 工单ID
     * @param dto 分配参数
     * @return 是否分配成功
     */
    boolean assignIssue(String issueId, AssignIssueDTO dto);

    /**
     * 更新工单状态
     * @param issueId 工单ID
     * @param status 状态
     * @return 是否更新成功
     */
    boolean updateIssueStatus(String issueId, String status);
}
