package org.davision1dyx.catmanguard.storage.convertor;

import org.davision1dyx.catmanguard.storage.model.FileChunk;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;

import java.util.UUID;

/**
 * @author Davison
 * @date 2026-05-04
 * @description
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, imports = {UUID.class})
public interface FileChunkConvertor {
    FileChunkConvertor INSTANCE = org.mapstruct.factory.Mappers.getMapper( FileChunkConvertor.class );

    @Mapping(target = "chunkId", expression = "UUID.randomUUID().toString()")
    @Mapping(source = "fileId", target = "fileId")
    @Mapping(source = "chunkIndex", target = "chunkIndex")
    @Mapping(source = "content", target = "content")
    @Mapping(source = "metaData", target = "metaData")
    FileChunk mapToModel(String fileId, Integer chunkIndex, String content, String metaData);
}
