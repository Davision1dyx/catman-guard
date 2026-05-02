package org.davision1dyx.catmanguard.api.conversation.service;

import reactor.core.publisher.Flux;

/**
 * @author Davison
 * @date 2026-04-30
 * @description 聊天服务
 */
public interface ChatService {

    Flux<String> chat(String conversationId, String query);

    Flux<String> webSearch(String conversationId, String query);

    boolean stop(String conversationId);
}
