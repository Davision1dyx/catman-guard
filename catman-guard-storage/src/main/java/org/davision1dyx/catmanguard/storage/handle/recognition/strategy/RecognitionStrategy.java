package org.davision1dyx.catmanguard.storage.handle.recognition.strategy;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Davison
 * @date 2026-05-02
 * @description 识别策略
 */
public interface RecognitionStrategy {

    String unzipTmpPath = "/home/catman/tmp";

    /**
     * 是否支持该文件类型
     *
     * @param fileType@return
     */
    boolean support(String fileType);

    /**
     * OCR识别，转换成可操作文件
     * @param file
     * @return
     * @throws IOException
     */
    String recognize(MultipartFile file) throws IOException;
}
