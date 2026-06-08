package org.davision1dyx.catmanguard.api.admin.service;

/**
 * @author Davison
 * @date 2026-06-02
 * @description 工单AI服务接口
 */
public interface IssueAIService {

    /**
     * 生成工单摘要
     * @param issueContent 工单内容
     * @return 摘要文本
     */
    String generateSummary(String issueContent);

    /**
     * 进行问题归因分析
     * @param issueContent 工单内容
     * @return 归因分析结果
     */
    String analyzeRootCause(String issueContent);
}