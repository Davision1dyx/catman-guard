package org.davision1dyx.catmanguard.storage.handle.recognition.strategy;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author Davison
 * @date 2026-05-02
 * @description 识别策略
 */
public interface RecognitionStrategy {

    String unzipTmpPath = "/Users/dyxfight/Document/catman";

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

    /**
     * OCR识别，转换成可操作文件
     * @param inputStream 输入流
     * @param fileName 文件名
     * @return
     * @throws IOException
     */
    String recognize(InputStream inputStream, String fileName) throws IOException;
}
