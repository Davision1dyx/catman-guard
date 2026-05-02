package org.davision1dyx.catmanguard.api.storage.service;

import org.davision1dyx.catmanguard.api.storage.dto.FileRecognizeDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileSplitDTO;
import org.davision1dyx.catmanguard.api.storage.dto.FileUploadDTO;
import org.davision1dyx.catmanguard.api.storage.vo.FileRecognizeVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileSplitVO;
import org.davision1dyx.catmanguard.api.storage.vo.FileUploadVO;

import java.io.IOException;

/**
 * @author Davison
 * @date 2026-05-01
 * @description
 */
public interface FileService {
    /**
     * 文件上传
     */
    FileUploadVO upload(FileUploadDTO fileUploadDTO);

    /**
     * 文件识别
     */
    FileRecognizeVO recognize(FileRecognizeDTO fileRecognizeDTO) throws IOException;

    /**
     * 文件切分
     */
    FileSplitVO split(FileSplitDTO fileSplitDTO);

    /**
     * 文件下载
     *
     */
    void download(String fileUrl);
}
