package org.davision1dyx.catmanguard.toolcall.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@ConfigurationProperties(prefix = "catman.toolcall")
public class ToolCallProperties {

    private McpProperties mcp;

    public static class McpProperties {
        private TavilyProperties tavily;

        public TavilyProperties getTavily() {
            return tavily;
        }

        public void setTavily(TavilyProperties tavily) {
            this.tavily = tavily;
        }
    }

    public static class TavilyProperties {
        private String key;
        private String url;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    public McpProperties getMcp() {
        return mcp;
    }

    public void setMcp(McpProperties mcp) {
        this.mcp = mcp;
    }
}
