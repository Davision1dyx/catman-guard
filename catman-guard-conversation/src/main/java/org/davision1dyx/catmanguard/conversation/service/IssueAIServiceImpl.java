package org.davision1dyx.catmanguard.conversation.service;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.admin.service.IssueAIService;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

/**
 * @author Davison
 * @date 2026-06-02
 * @description 工单AI服务实现
 */
@Slf4j
@Service
public class IssueAIServiceImpl implements IssueAIService {

    private final ChatClient chatClient;

    public IssueAIServiceImpl(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
    }

    @Override
    public String generateSummary(String issueContent) {
        if (issueContent == null || issueContent.trim().isEmpty()) {
            return "";
        }
        
        try {
            String prompt = """
                请对以下运维工单内容进行简明扼要的摘要，提取关键信息：
                1. 问题描述
                2. 影响范围
                3. 解决措施
                4. 最终结论
                
                工单内容：
                %s
                
                请用中文输出，保持简洁清晰。
                """.formatted(issueContent);

            String summary = chatClient.prompt()
                    .system("你是一个专业的运维工单分析助手，擅长提取关键信息并生成简洁的摘要。")
                    .user(prompt)
                    .call()
                    .content();
            
            log.info("Successfully generated summary for issue content (length: {})", issueContent.length());
            return summary;
            
        } catch (Exception e) {
            log.error("Failed to generate summary", e);
            throw new BizException(ErrorCode.ERROR, "工单摘要生成失败: " + e.getMessage());
        }
    }

    @Override
    public String analyzeRootCause(String issueContent) {
        if (issueContent == null || issueContent.trim().isEmpty()) {
            return "";
        }
        
        try {
            String prompt = """
                请对以下运维工单进行问题归因分析：
                
                工单内容：
                %s
                
                请按照以下结构输出分析结果：
                1. 问题现象描述
                2. 可能的根本原因（按可能性从高到低排序）
                3. 建议的预防措施
                4. 类似问题的关联分析
                
                请用中文输出，分析要专业、详细。
                """.formatted(issueContent);

            String rootCause = chatClient.prompt()
                    .system("你是一个资深的运维专家，擅长分析各类系统问题并找出根本原因。")
                    .user(prompt)
                    .call()
                    .content();
            
            log.info("Successfully analyzed root cause for issue content (length: {})", issueContent.length());
            return rootCause;
            
        } catch (Exception e) {
            log.error("Failed to analyze root cause", e);
            throw new BizException(ErrorCode.ERROR, "问题归因分析失败: " + e.getMessage());
        }
    }
}
