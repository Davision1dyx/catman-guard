package org.davision1dyx.catmanguard.conversation.advisor;

import org.davision1dyx.catmanguard.api.storage.dto.RetrievalDTO;
import org.davision1dyx.catmanguard.api.storage.service.RetrievalService;
import org.davision1dyx.catmanguard.api.storage.vo.RetrievalVO;
import org.davision1dyx.catmanguard.conversation.prompt.CommonPrompt;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Davison
 * @date 2026-05-05
 * @description
 */
@Component
public class RetrievalAdvisor implements BaseAdvisor {

    @Autowired
    private RetrievalService retrievalService;

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        UserMessage userMessage = chatClientRequest.prompt().getUserMessage();

        RetrievalDTO retrievalDTO = RetrievalDTO.builder()
                .topN(5)
                .similarity(0.5)
                .query(userMessage.getText())
                .build();
        RetrievalVO retrieveVO = retrievalService.retrieve(retrievalDTO);

        String content = retrieveVO.getContent();

        String retrievedMessage = CommonPrompt.RETRIEVAL_PROMPT
                .replace("{content}", content)
                .replace("{question}", userMessage.getText());

        chatClientRequest.prompt().augmentUserMessage(retrievedMessage);

        return chatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return null;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
