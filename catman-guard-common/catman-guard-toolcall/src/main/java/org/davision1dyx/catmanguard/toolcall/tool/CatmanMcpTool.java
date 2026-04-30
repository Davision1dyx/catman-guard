package org.davision1dyx.catmanguard.toolcall.tool;

import org.springframework.ai.tool.ToolCallback;

import java.util.List;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
public interface CatmanMcpTool {
    List<ToolCallback> initToolCallBack() throws Exception;
}
