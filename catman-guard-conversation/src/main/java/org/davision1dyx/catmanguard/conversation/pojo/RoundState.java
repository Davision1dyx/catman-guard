package org.davision1dyx.catmanguard.conversation.pojo;

import lombok.Data;
import org.davision1dyx.catmanguard.conversation.enums.RoundMode;
import org.springframework.ai.chat.messages.AssistantMessage;

import java.util.List;

import static java.util.Collections.synchronizedList;

/**
 * @author Davison
 * @date 2026-04-30
 * @description 轮次执行状态 保存每轮执行时的中间状态
 */
@Data
public class RoundState {
    /** 当前运行模式 */
    public RoundMode mode = RoundMode.UNKNOWN;

    /** 文本缓冲区 */
    public StringBuilder textBuffer = new StringBuilder();

    /** 工具调用列表 */
    public List<AssistantMessage.ToolCall> toolCalls = synchronizedList(new java.util.ArrayList<>());

}
