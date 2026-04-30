package org.davision1dyx.catmanguard.conversation.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.conversation.service.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author Davison
 * @date 2026-04-30
 * @description 聊天
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/conversation/chat/")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat")
    public Flux<String> chat(@RequestParam String message, @RequestParam String conversationId) {
        log.info("GET /processing/catman/conversation/chat/chat, message: {}, conversationId: {}", message, conversationId);
        return chatService.chat(conversationId, message);
    }

    @GetMapping("/websearch")
    public Flux<String> websearch(@RequestParam String message, @RequestParam String conversationId) {
        log.info("GET /processing/catman/conversation/chat/websearch, message: {}, conversationId: {}", message, conversationId);
        return chatService.webSearch(conversationId, message);
    }
}
