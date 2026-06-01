package org.davision1dyx.catmanguard.storage.convertor;

import org.davision1dyx.catmanguard.api.storage.dto.FileUploadDTO;
import org.davision1dyx.catmanguard.api.storage.vo.FileListVO;
import org.davision1dyx.catmanguard.base.util.FileUtil;
import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.davision1dyx.catmanguard.storage.enums.FileStatus;
import org.davision1dyx.catmanguard.storage.model.FileInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, imports = {FileStatus.class, FileMode.class, FileUtil.class, UUID.class})
public interface FileInfoConvertor {

    FileInfoConvertor INSTANCE = Mappers.getMapper(FileInfoConvertor.class);

    @Mapping(target = "fileId", expression = "java(UUID.randomUUID().toString())")
    @Mapping(source = "url", target = "url")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(target = "storageType", expression = "java(mode.name())")
    @Mapping(target = "status", expression = "java(FileStatus.UPLOADED.name())")
    @Mapping(target = "fileType", expression = "java(FileUtil.getFileType(fileUploadDTO.getFile().getOriginalFilename()))")
    FileInfo mapToModel(FileUploadDTO fileUploadDTO, String fileName, String url, FileMode mode);

    @Mapping(source = "fileId", target = "fileId")
    @Mapping(source = "fileTitle", target = "fileTitle")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "url", target = "fileUrl")
    @Mapping(target = "version", expression = "java(\"\")")
    @Mapping(source = "extension", target = "extension")
    @Mapping(source = "description", target = "description")
    @Mapping(target = "microservice", expression = "java(\"\")")
    @Mapping(source = "status", target = "status")
    @Mapping(target = "size", expression = "java(0L)")
    @Mapping(source = "createTime", target = "createTime")
    @Mapping(source = "updateTime", target = "updateTime")
    FileListVO mapToVo(FileInfo fileInfo);

}
