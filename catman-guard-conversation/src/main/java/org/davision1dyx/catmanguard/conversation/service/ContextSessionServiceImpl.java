package org.davision1dyx.catmanguard.conversation.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.davision1dyx.catmanguard.api.conversation.dto.SaveQuestionDTO;
import org.davision1dyx.catmanguard.api.conversation.dto.UpdateAnswerDTO;
import org.davision1dyx.catmanguard.api.conversation.service.ContextSessionService;
import org.davision1dyx.catmanguard.api.conversation.vo.ContextSessionVO;
import org.davision1dyx.catmanguard.conversation.convertor.ContextSessionConvertor;
import org.davision1dyx.catmanguard.conversation.mapper.ContextSessionMapper;
import org.davision1dyx.catmanguard.conversation.model.ContextSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Service
public class ContextSessionServiceImpl extends ServiceImpl<ContextSessionMapper, ContextSession> implements ContextSessionService {

    @Override
    public List<ContextSessionVO> findRecentBySessionId(String sessionId, int limit) {
        LambdaQueryWrapper<ContextSession> queryWrapper = new LambdaQueryWrapper<ContextSession>()
                .eq(ContextSession::getSessionId, sessionId)
                .orderByDesc(ContextSession::getCreateTime)
                .last("LIMIT " + limit);
        List<ContextSession> contextSessions = list(queryWrapper);
        return ContextSessionConvertor.INSTANCE.mapToVos(contextSessions);
    }

    @Override
    public ContextSessionVO saveQuestion(SaveQuestionDTO saveQuestionDTO) {
        ContextSession contextSession = new ContextSession();
        contextSession.setSessionId(saveQuestionDTO.getSessionId());
        contextSession.setQuestion(saveQuestionDTO.getQuestion());
        contextSession.setFileid(saveQuestionDTO.getFileid());
        contextSession.setTools(saveQuestionDTO.getTools());
        contextSession.setFirstResponseTime(saveQuestionDTO.getFirstResponseTime());
        save(contextSession);
        return ContextSessionConvertor.INSTANCE.mapToVo(contextSession);
    }

    @Override
    public boolean updateAnswer(UpdateAnswerDTO updateAnswerDTO) {
        ContextSession session = this.getById(updateAnswerDTO.getId());
        if (session != null) {
            session.setAnswer(updateAnswerDTO.getAnswer());
            session.setUpdateTime(LocalDateTime.now());
            if (updateAnswerDTO.getThinking() != null) {
                session.setThinking(updateAnswerDTO.getThinking());
            }
            if (updateAnswerDTO.getTools() != null) {
                session.setTools(updateAnswerDTO.getTools());
            }
            if (updateAnswerDTO.getReference() != null) {
                session.setReference(updateAnswerDTO.getReference());
            }
            if (updateAnswerDTO.getFirstResponseTime() != null) {
                session.setFirstResponseTime(updateAnswerDTO.getFirstResponseTime());
            }
            if (updateAnswerDTO.getTotalResponseTime() != null) {
                session.setTotalResponseTime(updateAnswerDTO.getTotalResponseTime());
            }
            if(updateAnswerDTO.getRecommend() != null){
                session.setRecommend(updateAnswerDTO.getRecommend());
            }
            return this.updateById(session);
        }
        return false;
    }
}
