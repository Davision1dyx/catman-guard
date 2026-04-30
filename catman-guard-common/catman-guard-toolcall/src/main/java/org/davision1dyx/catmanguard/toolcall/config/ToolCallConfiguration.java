package org.davision1dyx.catmanguard.toolcall.config;

import org.davision1dyx.catmanguard.toolcall.properties.ToolCallProperties;
import org.davision1dyx.catmanguard.toolcall.tool.CatmanMcpTool;
import org.davision1dyx.catmanguard.toolcall.tool.TavilyWebSearchTool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Configuration
@EnableConfigurationProperties(ToolCallProperties.class)
public class ToolCallConfiguration {

    @Bean
    public CatmanMcpTool tavilyMcpTool(ToolCallProperties toolCallProperties) {
        return new TavilyWebSearchTool(
                toolCallProperties.getMcp().getTavily().getKey(),
                toolCallProperties.getMcp().getTavily().getUrl()
        );
    }
}
