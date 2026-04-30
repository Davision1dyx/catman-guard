package org.davision1dyx.catmanguard.conversation.convertor;

import org.davision1dyx.catmanguard.api.conversation.vo.ContextSessionVO;
import org.davision1dyx.catmanguard.conversation.model.ContextSession;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ContextSessionConvertor {

    ContextSessionConvertor INSTANCE = Mappers.getMapper(ContextSessionConvertor.class);

    ContextSessionVO mapToVo(ContextSession contextSession);

    List<ContextSessionVO> mapToVos(List<ContextSession> contextSession);
}
