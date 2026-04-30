package org.davision1dyx.catmanguard.api.conversation.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Data
public class ContextSessionVO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 智能体类型（react/file/ppt）
     */
    private String agentType;

    /**
     * 用户问题
     */
    private String question;

    /**
     * AI回复
     */
    private String answer;

    /**
     * 涉及的执行工具名称（逗号分隔）
     */
    private String tools;

    /**
     * 参考链接JSON
     */
    private String reference;

    /**
     * 首次响应时间（毫秒）
     */
    private Long firstResponseTime;

    /**
     * 整体回复时间（毫秒）
     */
    private Long totalResponseTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 思考过程
     */
    private String thinking;

    /**
     * 关联文件ID（用于关联ai_file_info或ai_ppt_inst）
     */
    private String fileid;

    /**
     * 推荐问题JSON
     */
    private String recommend;
}
