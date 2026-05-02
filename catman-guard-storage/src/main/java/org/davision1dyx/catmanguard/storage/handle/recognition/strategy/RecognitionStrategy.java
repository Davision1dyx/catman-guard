package org.davision1dyx.catmanguard.storage.handle.recognition.strategy;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author Davison
 * @date 2026-05-02
 * @description 识别策略
 */
public interface RecognitionStrategy {

    boolean support(MultipartFile multipartFile);

    String recognize(MultipartFile file) throws IOException;
}
