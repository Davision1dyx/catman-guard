package org.davision1dyx.catmanguard.conversation.service;

import org.davision1dyx.catmanguard.api.conversation.service.ChatService;
import org.davision1dyx.catmanguard.api.conversation.service.ContextSessionService;
import org.davision1dyx.catmanguard.conversation.agent.ChatAgent;
import org.davision1dyx.catmanguard.conversation.agent.WebSearchAgent;
import org.davision1dyx.catmanguard.conversation.manager.AgentTaskManager;
import org.davision1dyx.catmanguard.conversation.prompt.CommonPrompt;
import org.davision1dyx.catmanguard.toolcall.tool.CatmanMcpTool;
import org.davision1dyx.catmanguard.toolcall.tool.TavilyWebSearchTool;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Service
public class ChatServiceImpl implements ChatService {

    private final ChatModel chatModel;
    private final AgentTaskManager agentTaskManager;
    private final ContextSessionService contextSessionService;
    private final List<ToolCallback> tavilyToolCallbacks;

    public ChatServiceImpl(ChatModel chatModel, AgentTaskManager agentTaskManager, ContextSessionService contextSessionService,
                           CatmanMcpTool tavilyWebSearchTool) throws Exception {
        this.chatModel = chatModel;
        this.agentTaskManager = agentTaskManager;
        this.contextSessionService = contextSessionService;
        this.tavilyToolCallbacks = tavilyWebSearchTool.initToolCallBack();
    }

    @Override
    public Flux<String> chat(String conversationId, String query) {

        ChatAgent chatAgent = ChatAgent.builder()
                .chatModel(chatModel)
                .name("chatAgent")
                .maxRound(5)
                .systemPrompt(CommonPrompt.ROLE_DEFINE)
                .contextSessionService(contextSessionService)
                .taskManager(agentTaskManager)
                .build();

        ChatMemory persistentChatMemory = chatAgent.createPersistentChatMemory(conversationId, 20);
        chatAgent.setChatMemory(persistentChatMemory);

        return chatAgent.stream(query, conversationId);
    }

    @Override
    public Flux<String> webSearch(String conversationId, String query) {
        WebSearchAgent webSearchAgent = WebSearchAgent.builder()
                .systemPrompt(CommonPrompt.ROLE_DEFINE)
                .chatModel(chatModel)
                .name("websearchAgent")
                .contextSessionService(contextSessionService)
                .taskManager(agentTaskManager)
                .maxRound(5)
                .tools(tavilyToolCallbacks)
                .build();
        ChatMemory persistentChatMemory = webSearchAgent.createPersistentChatMemory(conversationId, 20);
        webSearchAgent.setChatMemory(persistentChatMemory);

        return webSearchAgent.stream(query, conversationId);
    }

    @Override
    public boolean stop(String conversationId) {
        return agentTaskManager.stopTask(conversationId);
    }
}
