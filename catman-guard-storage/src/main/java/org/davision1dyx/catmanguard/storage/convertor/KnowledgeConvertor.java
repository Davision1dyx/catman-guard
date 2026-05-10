package org.davision1dyx.catmanguard.storage.convertor;

import org.davision1dyx.catmanguard.api.storage.vo.KnowledgeVO;
import org.davision1dyx.catmanguard.storage.model.Knowledge;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;
import org.mapstruct.MappingConstants;

import java.time.format.DateTimeFormatter;

@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, imports = {DateTimeFormatter.class})
public interface KnowledgeConvertor {

    KnowledgeConvertor INSTANCE = Mappers.getMapper(KnowledgeConvertor.class);

    @Mapping(source = "knowledgeId", target = "knowledgeId")
    @Mapping(source = "name", target = "name")
    @Mapping(source = "description", target = "description")
    @Mapping(source = "type", target = "type")
    @Mapping(source = "fileCount", target = "fileCount")
    @Mapping(source = "chunkCount", target = "chunkCount")
    @Mapping(target = "createdTime", expression = "java(knowledge.getCreateTime() != null ? knowledge.getCreateTime().format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd'T'HH:mm:ss\")) : null)")
    @Mapping(target = "updatedTime", expression = "java(knowledge.getUpdateTime() != null ? knowledge.getUpdateTime().format(DateTimeFormatter.ofPattern(\"yyyy-MM-dd'T'HH:mm:ss\")) : null)")
    KnowledgeVO mapToVO(Knowledge knowledge);
}