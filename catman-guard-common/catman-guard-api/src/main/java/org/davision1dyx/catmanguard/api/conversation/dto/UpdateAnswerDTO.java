package org.davision1dyx.catmanguard.api.conversation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateAnswerDTO {
    /**
     * 记录ID（必填）
     */
    private Long id;

    /**
     * AI回复（必填）
     */
    private String answer;

    /**
     * 思考过程（可选）
     */
    private String thinking;

    /**
     * 使用的工具名称（逗号分隔，可选）
     */
    private String tools;

    /**
     * 参考链接JSON（可选）
     */
    private String reference;

    /**
     * 首次响应时间（毫秒，可选）
     */
    private Long firstResponseTime;

    /**
     * 总响应时间（毫秒，可选）
     */
    private Long totalResponseTime;

    /**
     * 推荐问题JSON（可选）
     */
    private String recommend;
}
