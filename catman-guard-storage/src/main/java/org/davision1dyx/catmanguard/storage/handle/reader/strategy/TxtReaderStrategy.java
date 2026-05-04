package org.davision1dyx.catmanguard.storage.handle.reader.strategy;

import org.davision1dyx.catmanguard.storage.enums.FileType;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.ByteArrayResource;

import java.util.List;

/**
* @author Davison
* @date 2026-05-02
* @description TXT读取策略
*/
public class TxtReaderStrategy implements ReaderStrategy {
    @Override
    public boolean support(String fileType) {
        return FileType.TXT.suffix.contains(fileType);
    }

    @Override
    public List<Document> read(byte[] bytes) {
        TextReader reader = new TextReader(new ByteArrayResource(bytes));
        return reader.read();
    }
}
