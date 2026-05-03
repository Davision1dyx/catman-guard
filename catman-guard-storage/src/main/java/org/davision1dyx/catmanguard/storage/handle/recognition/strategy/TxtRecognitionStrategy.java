package org.davision1dyx.catmanguard.storage.handle.recognition.strategy;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
* @author Davison
* @date 2026-05-02
* @description TXT识别策略
*/
public class TxtRecognitionStrategy implements RecognitionStrategy{
    @Override
    public boolean support(String fileType) {
        return false;
    }

    @Override
    public String recognize(MultipartFile file) {
        return null;
    }

    @Override
    public List<Document> read(byte[] bytes) {
        TextReader reader = new TextReader(new ByteArrayResource(bytes));
        return reader.read();
    }
}
