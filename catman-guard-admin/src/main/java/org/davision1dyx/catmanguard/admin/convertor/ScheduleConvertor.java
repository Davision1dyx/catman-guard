package org.davision1dyx.catmanguard.admin.convertor;

import org.davision1dyx.catmanguard.admin.model.Schedule;
import org.davision1dyx.catmanguard.api.admin.vo.ScheduleVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface ScheduleConvertor {

    ScheduleConvertor INSTANCE = Mappers.getMapper(ScheduleConvertor.class);

    @Mapping(source = "scheduleId", target = "id")
    ScheduleVO mapToVO(Schedule schedule);
}