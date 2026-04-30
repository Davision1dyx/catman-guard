package org.davision1dyx.catmanguard.toolcall.tool;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.tool.ToolCallback;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.security.cert.X509Certificate;
import java.time.Duration;
import java.util.List;

/**
 * @author Davison
 * @date 2026-04-18
 * @description
 */
@Slf4j
public class TavilyWebSearchTool implements CatmanMcpTool {

    /**
     * Tavily 搜索引擎 API Key
     */
    private final String apiKey;
    /**
     * Tavily MCP URL
     */
    private final String mcpUrl;

    public TavilyWebSearchTool(String apiKey, String mcpUrl) {
        this.apiKey = apiKey;
        this.mcpUrl = mcpUrl;
    }

    @Override
    public List<ToolCallback> initToolCallBack() throws Exception {
        log.info("初始化网页搜索工具回调...");

        // 1. 创建信任所有证书的 TrustManager
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() { return null; }
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        // 2. 初始化 SSLContext
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

        // tavily 搜索引擎
        String authorizationHeader = "Bearer " + apiKey;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .header("Authorization", authorizationHeader);

        HttpClientStreamableHttpTransport tavTransport = HttpClientStreamableHttpTransport.builder(mcpUrl)
                .clientBuilder(HttpClient.newBuilder().sslContext(sslContext))
                .requestBuilder(requestBuilder).build();
        McpSyncClient tavilyMcp = McpClient.sync(tavTransport)
                .requestTimeout(Duration.ofSeconds(120))
                .build();
        tavilyMcp.initialize();

        List<McpSyncClient> mcpClients = List.of(tavilyMcp);
        SyncMcpToolCallbackProvider provider = SyncMcpToolCallbackProvider.builder().mcpClients(mcpClients).build();
        ToolCallback[] toolCallbacks = provider.getToolCallbacks();
        log.info("网页搜索工具回调初始化完成，工具数量: {}", toolCallbacks.length);
        return List.of(toolCallbacks);
    }
}
