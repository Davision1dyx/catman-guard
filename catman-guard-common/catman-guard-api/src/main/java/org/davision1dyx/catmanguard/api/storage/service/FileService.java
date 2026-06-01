package org.davision1dyx.catmanguard.api.storage.service;

import org.davision1dyx.catmanguard.api.storage.dto.FileListDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileRecognizeDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileSplitDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileUploadDTO;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileListVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileRecognizeVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileSplitVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileUploadVO;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * @author Davison
 * @date 2026-05-01
 * @description 文件服务
 */
public interface FileService {
    /**
     * 文件上传
     */
    FileUploadVO upload(FileUploadDTO fileUploadDTO);

    List<FileListVO> list(FileListDTO fileListDTO);

    void delete(String fileId);

    /**
     * 文件识别
     */
    FileRecognizeVO recognize(FileRecognizeDTO fileRecognizeDTO) throws IOException;

    /**
     * 文件切分
     */
    FileSplitVO split(FileSplitDTO fileSplitDTO);

    /**
     * 执行分片并返回分片结果
     */
    ChunkVO chunk(FileSplitDTO fileSplitDTO);

    /**
     * 获取文件信息
     * @param fileId 文件ID
     * @return 文件信息VO
     */
    FileListVO getFileInfo(String fileId);

    /**
     * 流式文件下载
     * @param fileId 文件ID
     * @param outputStream 输出流
     */
    void downloadToStream(String fileId, OutputStream outputStream);
}
