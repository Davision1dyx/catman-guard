package org.davision1dyx.catmanguard.conversation.service;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.conversation.service.MultiModalService;
import org.davision1dyx.catmanguard.base.constant.CommonConstant;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.content.Media;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

import java.net.URI;
import java.util.List;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@Slf4j
@Service
public class MultiModalServiceImpl implements MultiModalService {

    public final ChatClient chatClient;

    public MultiModalServiceImpl(ChatModel chatModel) {
        this.chatClient = initChatClient(chatModel);
    }

    @Override
    public String generateImageDescription(String imageUrl) {
        try {
            List<Media> mediaList = List.of(
                    new Media(
                            MimeTypeUtils.IMAGE_PNG,
                            new URI(imageUrl)
                                    .toURL()
                                    .toURI()
                    )
            );
            UserMessage message = UserMessage.builder().text(CommonConstant.EMPTY).media(mediaList).build();

        return chatClient.prompt()
                .system("请描述这张图片的内容，包括场景、对象、布局、颜色、文字信息，直接输出纯文本描述，不要多余说明。")
                .messages(message)
                .call()
                .content();
        } catch (Exception e) {
            log.error("Failed to generate image description", e);
            throw new BizException(ErrorCode.ERROR, "Failed to generate image description");
        }
    }

    private ChatClient initChatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultOptions(ChatOptions.builder().model("qwen3-vl-plus").build())
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
}
