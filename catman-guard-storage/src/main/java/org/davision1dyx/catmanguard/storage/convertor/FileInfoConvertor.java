package org.davision1dyx.catmanguard.storage.convertor;

import org.davision1dyx.catmanguard.api.storage.dto.FileUploadDTO;
import org.davision1dyx.catmanguard.base.util.FileUtil;
import org.davision1dyx.catmanguard.file.enums.FileMode;
import org.davision1dyx.catmanguard.storage.enums.FileStatus;
import org.davision1dyx.catmanguard.storage.model.FileInfo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.factory.Mappers;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@Mapper(nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS, imports = {FileStatus.class, FileMode.class, FileUtil.class})
public interface FileInfoConvertor {

    FileInfoConvertor INSTANCE = Mappers.getMapper(FileInfoConvertor.class);

    @Mapping(source = "url", target = "url")
    @Mapping(source = "fileName", target = "fileName")
    @Mapping(source = "mode.name()", target = "storageType")
    @Mapping(target = "status", expression = "java(FileStatus.INIT.name())")
    @Mapping(target = "fileType", expression = "java(FileUtil.getFileType(fileUploadDTO.getFile().getOriginalFilename()))")
    FileInfo mapToModel(FileUploadDTO fileUploadDTO, String fileName, String url, FileMode mode);

}
