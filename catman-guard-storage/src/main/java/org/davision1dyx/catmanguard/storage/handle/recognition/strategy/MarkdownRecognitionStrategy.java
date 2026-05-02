package org.davision1dyx.catmanguard.storage.handle.recognition.strategy;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Davison
 * @date 2026-05-02
 * @description MARKDOWN识别策略
 */
public class MarkdownRecognitionStrategy implements RecognitionStrategy{
    @Override
    public boolean support(MultipartFile multipartFile) {
        return false;
    }

    @Override
    public String recognize(MultipartFile file) {
        return null;
    }
}
