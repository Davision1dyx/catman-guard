package org.davision1dyx.catmanguard.api.conversation.service;

import org.davision1dyx.catmanguard.api.conversation.dto.SaveQuestionDTO;
import org.davision1dyx.catmanguard.api.conversation.dto.UpdateAnswerDTO;
import org.davision1dyx.catmanguard.api.conversation.vo.ContextSessionVO;

import java.util.List;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
public interface ContextSessionService {

    List<ContextSessionVO> findRecentBySessionId(String sessionId, int limit);

    ContextSessionVO saveQuestion(SaveQuestionDTO saveQuestionDTO);

    boolean updateAnswer(UpdateAnswerDTO updateAnswerDTO);
}
