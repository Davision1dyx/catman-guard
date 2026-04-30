package org.davision1dyx.catmanguard.conversation.agent;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.conversation.dto.SaveQuestionDTO;
import org.davision1dyx.catmanguard.api.conversation.dto.UpdateAnswerDTO;
import org.davision1dyx.catmanguard.api.conversation.service.ContextSessionService;
import org.davision1dyx.catmanguard.api.conversation.vo.ContextSessionVO;
import org.davision1dyx.catmanguard.conversation.enums.RoundMode;
import org.davision1dyx.catmanguard.conversation.manager.AgentTaskManager;
import org.davision1dyx.catmanguard.conversation.model.ContextSession;
import org.davision1dyx.catmanguard.conversation.pojo.AgentState;
import org.davision1dyx.catmanguard.conversation.pojo.RoundState;
import org.davision1dyx.catmanguard.conversation.prompt.CommonPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.messages.*;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.util.StringUtils;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Slf4j
public class ChatAgent extends AbstractAgent {

    private final String systemPrompt;
    private final ChatClient chatClient;
    private final List<Advisor> advisors;
    private final Integer maxRound;
    private final List<ToolCallback> toolCallbacks;


    public ChatAgent(String name, String systemPrompt, ChatModel chatModel, List<Advisor> advisors,
                     Integer maxRound, List<ToolCallback> toolCallbacks, ContextSessionService contextSessionService,
                     AgentTaskManager agentTaskManager) {
        super(name, chatModel, "chatAgent");
        this.systemPrompt = systemPrompt;
        this.advisors = advisors;
        this.maxRound = maxRound;
        this.toolCallbacks = toolCallbacks;
        this.contextSessionService = contextSessionService;
        this.taskManager = agentTaskManager;


        chatClient = initChatClient(chatModel);
    }

    private ChatClient initChatClient(ChatModel chatModel) {
        ToolCallingChatOptions toolCallingChatOptions = ToolCallingChatOptions.builder()
                .internalToolExecutionEnabled(false)
                .build();
        ChatClient.Builder builder = ChatClient.builder(chatModel)
                .defaultOptions(toolCallingChatOptions);
        if (advisors != null && !advisors.isEmpty()) {
            builder.defaultAdvisors(advisors);
        }
        return builder.build();
    }

    @Override
    public Flux<String> execute(String question, String conversationId) {
        return null;
    }

    public Flux<String> stream(String question) {
        return streamInternal(question, null);
    }

    public Flux<String> stream(String question, String conversationId) {
        return streamInternal(question, conversationId);
    }

    private Flux<String> streamInternal(String question, String conversationId) {

        // 检查是否已有任务在运行
        Flux<String> checkResult = checkRunningTask(conversationId);
        if (checkResult != null) {
            return checkResult;
        }

        // 初始化
        initTimers();
        clearUsedTools();
        Sinks.Many<String> sinks = Sinks.many().unicast().onBackpressureBuffer();

        // 注册
        AgentTaskManager.TaskInfo taskInfo = registerTask(conversationId, sinks);

        if (taskInfo == null && conversationId != null && taskManager != null) {
            return Flux.error(new IllegalStateException("该会话正在执行中，请稍后再试"));
        }

        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(CommonPrompt.CHAT_PROMPT));

        if (StringUtils.hasText(systemPrompt)) {
            messages.add(new SystemMessage(systemPrompt));
        }
        loadChatHistory(conversationId, messages, true, true);

        messages.add(new UserMessage("<question> " + question + " </question>"));
        currentQuestion = question;

        // Memory
        // 添加记忆
        boolean useMemory = StringUtils.hasText(conversationId) || chatMemory != null;
        if (useMemory && contextSessionService != null) {
            ContextSessionVO contextSessionVO = contextSessionService.saveQuestion(SaveQuestionDTO.builder()
                    .sessionId(conversationId)
                    .question(question)
                    .build());
            currentSessionId = contextSessionVO.getId();
            chatMemory.add(conversationId, new UserMessage(question));
        } else if (useMemory) {
            chatMemory.add(conversationId, new UserMessage(question));
        }


        // 迭代轮次
        AtomicLong roundCounter = new AtomicLong(0);
        // 是否发送最终结果标记位
        AtomicBoolean hasSentFinalResult = new AtomicBoolean(false);

        hasSentFinalResult.set(false);
        roundCounter.set(0);

        // 收集最终答案（纯文本），存储memory
        StringBuilder finalAnswerBuffer = new StringBuilder();
        // 收集思考过程
        StringBuilder thinkingBuffer = new StringBuilder();

        AgentState agentState = new AgentState();

        // 在这里，多轮写入sink
        scheduleRound(messages, sinks, roundCounter, hasSentFinalResult, finalAnswerBuffer, useMemory,
                conversationId, agentState, thinkingBuffer);

        return sinks.asFlux()
            .doOnNext(chunk -> {
                // 记录第一次响应时间
                recordFirstResponse();
                try {
                    // 解析 JSON，如果是 type=text，则只拼接 content；如果是 type=thinking，则拼接 thinking
                    JSONObject json = JSON.parseObject(chunk);
                    String type = json.getString("type");
                    if ("text".equals(type)) {
                        finalAnswerBuffer.append(json.getString("content"));
                    } else if ("thinking".equals(type)) {
                        thinkingBuffer.append(json.getString("content"));
                    }
                } catch (Exception e) {
                    // 解析失败，直接拼接
                    finalAnswerBuffer.append(chunk);
                }
            })
            .doOnCancel(() -> {
                hasSentFinalResult.set(true);
                removeTask(conversationId);
            })
            .doFinally(signalType -> {
                log.info("最终答案: {}", finalAnswerBuffer);
                log.info("思考过程: {}", thinkingBuffer);

                // 保存结果到会话
                saveSessionResult(conversationId, finalAnswerBuffer, thinkingBuffer);

                // 流结束时移除任务
                removeTask(conversationId);
            });
    }

    private void scheduleRound(List<Message> messages, Sinks.Many<String> sinks, AtomicLong roundCounter,
                               AtomicBoolean hasSentFinalResult, StringBuilder finalAnswerBuffer,
                               boolean useMemory, String conversationId, AgentState agentState,
                               StringBuilder thinkingBuffer) {
        // 轮次+1
        roundCounter.incrementAndGet();
        RoundState roundState = new RoundState();

        Disposable disposable = chatClient.prompt()
                .messages(messages)
                .stream()
                .chatResponse()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(chunck -> processChunk(chunck, sinks, roundState))
                .doOnComplete(() -> finishRound(messages, sinks, roundState, roundCounter, hasSentFinalResult,
                        finalAnswerBuffer, useMemory, conversationId, agentState, thinkingBuffer))
                .doOnError(err -> {
                    if (hasSentFinalResult.get()) {
                        return;
                    }
                    hasSentFinalResult.set(true);
                    sinks.tryEmitError(err);
                })
                .subscribe();
        // 保存Disposable到任务管理器
        if (StringUtils.hasText(conversationId) && taskManager != null) {
            taskManager.setDisposable(conversationId, disposable);
        }

    }

    private void finishRound(List<Message> messages, Sinks.Many<String> sinks, RoundState roundState,
                             AtomicLong roundCounter, AtomicBoolean hasSentFinalResult, StringBuilder finalAnswerBuffer,
                             boolean useMemory, String conversationId, AgentState agentState, StringBuilder thinkingBuffer) {

        // 如果整轮都没有 tool_call，才是最终答案
        if (roundState.getMode() != RoundMode.TOOL_CALL) {
            String referenceJson = "";
            String toolsStr = getUsedToolsString();
            String finalText = roundState.textBuffer.toString();

            // 输出参考链接
            if (!agentState.searchResults.isEmpty()) {
                String reference = JSON.toJSONString(agentState.searchResults);
                referenceJson = createReferenceResponse(reference);
                sinks.tryEmitNext(referenceJson);
            }

            // 输出推荐问题
            if (enableRecommendations) {
                String recommendations = generateRecommendations(conversationId, currentQuestion, finalText);
                if (recommendations != null) {
                    currentRecommendations = recommendations; // 保存用于数据库存储
                    String recommendJson = createRecommendResponse(recommendations);
                    sinks.tryEmitNext(recommendJson);
                }
            }

            sinks.tryEmitComplete();
            hasSentFinalResult.set(true);

            if (useMemory) {
                chatMemory.add(conversationId, new AssistantMessage(finalText));
            }
            return;
        }

        // TOOL_CALL
        AssistantMessage assistantMsg = AssistantMessage.builder().toolCalls(roundState.toolCalls).build();
        messages.add(assistantMsg);

        if (maxRound > 0 && roundCounter.get() >= maxRound) {
            forceFinalStream(messages, sinks, hasSentFinalResult, roundState, conversationId, useMemory, agentState, thinkingBuffer);
            return;
        }

        executeToolCalls(sinks, roundState.toolCalls, messages, hasSentFinalResult, roundState, agentState, () -> {
            if (!hasSentFinalResult.get()) {
                // 结束工具调用完成，继续下一轮
                scheduleRound(messages, sinks, roundCounter,
                        hasSentFinalResult, finalAnswerBuffer,
                        useMemory, conversationId, agentState, thinkingBuffer);
            }
        });
    }

    private void forceFinalStream(List<Message> messages, Sinks.Many<String> sink, AtomicBoolean hasSentFinalResult, RoundState state,
                                  String conversationId, boolean useMemory, AgentState agentState, StringBuilder thinkingBuffer) {
        // 创建新的消息列表，确保系统提示词在最前面
        List<Message> newMessages = new ArrayList<>();

        // 添加系统提示词
        newMessages.add(new SystemMessage(CommonPrompt.WEB_SEARCH_PROMPT));
        if (org.apache.commons.lang3.StringUtils.isNotBlank(systemPrompt)) {
            newMessages.add(new SystemMessage(systemPrompt));
        }

        // 添加原有消息（跳过系统消息）
        for (Message msg : messages) {
            if (!(msg instanceof SystemMessage)) {
                newMessages.add(msg);
            }
        }

        // 添加限制提示
        newMessages.add(new UserMessage("""
                你已达到最大推理轮次限制。
                请基于当前已有的上下文信息，
                直接给出最终答案。
                禁止再调用任何工具。
                如果信息不完整，请合理总结和说明。
                """));

        // 替换原消息列表
        messages.clear();
        messages.addAll(newMessages);

        // 收集最终文本
        StringBuilder finalTextBuffer = new StringBuilder();

        Disposable disposable = chatClient.prompt()
                .messages(messages)
                .stream()
                .chatResponse()
                .publishOn(Schedulers.boundedElastic())
                .doOnNext(chunk -> {
                    if (chunk == null || chunk.getResult() == null || chunk.getResult().getOutput() == null) {
                        return;
                    }

                    String text = chunk.getResult()
                            .getOutput()
                            .getText();

                    if (text != null && !hasSentFinalResult.get()) {
                        sink.tryEmitNext(createTextResponse(text));
                        finalTextBuffer.append(text);
                    }
                })
                .doOnComplete(() -> {
                    String referenceJson = "";
                    String finalText = finalTextBuffer.toString();

                    // 输出参考链接
                    if (!agentState.searchResults.isEmpty()) {
                        String reference = JSON.toJSONString(agentState.searchResults);
                        referenceJson = createReferenceResponse(reference);
                        sink.tryEmitNext(referenceJson);
                    }

                    // 输出推荐问题
                    if (enableRecommendations) {
                        String recommendations = generateRecommendations(conversationId, currentQuestion, finalText);
                        if (recommendations != null) {
                            currentRecommendations = recommendations; // 保存用于数据库存储
                            String recommendJson = createRecommendResponse(recommendations);
                            sink.tryEmitNext(recommendJson);
                        }
                    }

                    if (useMemory) {
                        chatMemory.add(conversationId, new AssistantMessage(finalText));
                    }

                    hasSentFinalResult.set(true);
                    sink.tryEmitComplete();
                })
                .doOnError(err -> {
                    hasSentFinalResult.set(true);
                    sink.tryEmitError(err);
                })
                .subscribe();

        // 保存Disposable到任务管理器
        if (conversationId != null && taskManager != null) {
            taskManager.setDisposable(conversationId, disposable);
        }
    }

    private void executeToolCalls(Sinks.Many<String> sink, List<AssistantMessage.ToolCall> toolCalls, List<Message> messages,
                                  AtomicBoolean hasSentFinalResult, RoundState state, AgentState agentState, Runnable onComplete) {
        AtomicInteger completedCount = new AtomicInteger(0);
        int totalToolCalls = toolCalls.size();

        for (AssistantMessage.ToolCall tc : toolCalls) {
            Schedulers.boundedElastic().schedule(() -> {
                if (hasSentFinalResult.get()) {
                    completeToolCall(completedCount, totalToolCalls, onComplete);
                    return;
                }

                String toolName = tc.name();
                String argsJson = tc.arguments();

                ToolCallback callback = findTool(toolName);
                if (callback == null) {
                    addErrorToolResponse(messages, tc, "工具未找到：" + toolName);
                    completeToolCall(completedCount, totalToolCalls, onComplete);
                    return;
                }
                if (toolName.contains("search")) {
                    JSONObject args = JSON.parseObject(argsJson);
                    String query = (String) args.get("query");
                    // 发送 thinking 消息，表示正在搜索相关信息     这里是模拟使用搜索引擎工具搜索信息
                    String queryThink = org.apache.commons.lang3.StringUtils.isNotBlank(query) ? "🔍 正在搜索信息: " + query + "\n" : "🔍 正在搜索相关信息\n";
                    sink.tryEmitNext(createThinkingResponse(queryThink));
                }

                try {
                    Object result = callback.call(argsJson);
                    ToolResponseMessage.ToolResponse tr = new ToolResponseMessage.ToolResponse(
                            tc.id(), toolName, result.toString());
                    messages.add(ToolResponseMessage.builder()
                            .responses(List.of(tr))
                            .build());

                    // 记录使用的工具
                    recordUsedTool(toolName);
                } catch (Exception ex) {
                    addErrorToolResponse(messages, tc, "工具执行失败：" + ex.getMessage());
                } finally {
                    completeToolCall(completedCount, totalToolCalls, onComplete);
                }
            });
        }
    }

    private void processChunk(ChatResponse chunk, Sinks.Many<String> sink, RoundState state) {
        if (chunk == null || chunk.getResult() == null ||
                chunk.getResult().getOutput() == null) {
            return;
        }

        Generation gen = chunk.getResult();
        String text = gen.getOutput().getText();
        List<AssistantMessage.ToolCall> tc = gen.getOutput().getToolCalls();

        // 一旦发现 tool_call，立即进入 TOOL_CALL 模式
        if (tc != null && !tc.isEmpty()) {
            state.mode = RoundMode.TOOL_CALL;

            for (AssistantMessage.ToolCall incoming : tc) {
                mergeToolCall(state, incoming);
            }
            return;
        }

        // 还没出现 tool_call，发送并缓存文本
        if (text != null) {
            // Sink.Many  发送文本
            sink.tryEmitNext(createTextResponse(text));
            state.textBuffer.append(text);
        }
    }

    private void mergeToolCall(RoundState state, AssistantMessage.ToolCall incoming) {
        for (int i = 0; i < state.toolCalls.size(); i++) {
            AssistantMessage.ToolCall existing = state.toolCalls.get(i);

            if (existing.id().equals(incoming.id())) {
                // 或者是一次没有完全的拿到整个工具调用参数？
                String mergedArgs = Objects.toString(existing.arguments(), "") +
                        Objects.toString(incoming.arguments(), "");

                state.toolCalls.set(i,
                        new AssistantMessage.ToolCall(existing.id(), "function", existing.name(), mergedArgs)
                );
                return;
            }
        }

        // 新的 toolcall
        state.toolCalls.add(incoming);
    }

    /**
     * 保存会话结果
     */
    private void saveSessionResult(String conversationId, StringBuilder finalAnswerBuffer,
                                   StringBuilder thinkingBuffer) {
        if (contextSessionService != null && currentSessionId != null && finalAnswerBuffer.length() > 0) {
            long totalResponseTime = getTotalResponseTime();
            String toolsStr = getUsedToolsString();

            UpdateAnswerDTO request = UpdateAnswerDTO.builder()
                    .id(currentSessionId)
                    .answer(finalAnswerBuffer.toString())
                    .thinking(thinkingBuffer.toString())
                    .tools(toolsStr)
                    .recommend(currentRecommendations)
                    .firstResponseTime(firstResponseTime)
                    .totalResponseTime(totalResponseTime)
                    .build();
            contextSessionService.updateAnswer(request);
            log.info("结果已保存到会话: sessionId={}", conversationId);
        }
    }

    private void addErrorToolResponse(List<Message> messages, AssistantMessage.ToolCall toolCall, String errMsg) {
        ToolResponseMessage.ToolResponse tr = new ToolResponseMessage.ToolResponse(
                toolCall.id(),
                toolCall.name(),
                "{ \"error\": \"" + errMsg + "\" }"
        );

        messages.add(ToolResponseMessage.builder()
                .responses(List.of(tr))
                .build());
    }

    private ToolCallback findTool(String name) {
        return toolCallbacks.stream()
                .filter(t -> t.getToolDefinition().name().equals(name))
                .findFirst()
                .orElse(null);
    }

    private void completeToolCall(AtomicInteger completedCount, int total, Runnable onComplete) {
        int current = completedCount.incrementAndGet();
        if (current >= total) {
            onComplete.run();
        }
    }


    public static ChatAgentBuilder builder() {
        return new ChatAgentBuilder();
    }

    public static class ChatAgentBuilder {

        private String name;
        private ChatModel chatModel;
        private String systemPrompt;
        private List<ToolCallback> toolCallbacks;
        private List<Advisor> advisors;
        private Integer maxRound;
        private ContextSessionService contextSessionService;
        private AgentTaskManager agentTaskManager;

        public ChatAgentBuilder name(String name) {
            this.name = name;
            return this;
        }

        public ChatAgentBuilder chatModel(ChatModel chatModel) {
            this.chatModel = chatModel;
            return this;
        }

        public ChatAgentBuilder systemPrompt(String systemPrompt) {
            this.systemPrompt = systemPrompt;
            return this;
        }

        public ChatAgentBuilder advisors(List<Advisor> advisors) {
            this.advisors = advisors;
            return this;
        }

        public ChatAgentBuilder advisors(Advisor... advisors) {
            this.advisors = Arrays.asList(advisors);
            return this;
        }

        public ChatAgentBuilder maxRound(Integer maxRound) {
            this.maxRound = maxRound;
            return this;
        }

        public ChatAgentBuilder toolCallbacks(List<ToolCallback> toolCallbacks) {
            this.toolCallbacks = toolCallbacks;
            return this;
        }

        public ChatAgentBuilder contextSessionService(ContextSessionService contextSessionService) {
            this.contextSessionService = contextSessionService;
            return this;
        }

        public ChatAgentBuilder taskManager(AgentTaskManager agentTaskManager) {
            this.agentTaskManager = agentTaskManager;
            return this;
        }

        public ChatAgent build() {
            return new ChatAgent(name, systemPrompt, chatModel, advisors, maxRound, toolCallbacks, contextSessionService, agentTaskManager);
        }
    }
}
