package org.davision1dyx.catmanguard.admin.convertor;

import org.davision1dyx.catmanguard.admin.model.Staff;
import org.davision1dyx.catmanguard.api.admin.vo.StaffVO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
public interface StaffConvertor {

    StaffConvertor INSTANCE = Mappers.getMapper(StaffConvertor.class);

    @Mapping(source = "staffId", target = "id")
    @Mapping(source = "createTime", target = "joinedTime")
    StaffVO mapToVO(Staff staff);
}