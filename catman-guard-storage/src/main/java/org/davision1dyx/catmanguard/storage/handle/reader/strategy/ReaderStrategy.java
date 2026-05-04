package org.davision1dyx.catmanguard.storage.handle.reader.strategy;

import org.springframework.ai.document.Document;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-02
 * @description 读取策略
 */
public interface ReaderStrategy {

    String unzipTmpPath = "/home/catman/tmp";

    /**
     * 是否支持该文件类型
     *
     * @param fileType@return
     */
    boolean support(String fileType);

    /**
     * 读取文件内容
     *
     * @param bytes@return
     */
    List<Document> read(byte[] bytes);
}
