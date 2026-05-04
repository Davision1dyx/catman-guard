package org.davision1dyx.catmanguard.storage.handle.reader.strategy;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.storage.enums.FileType;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-02
 * @description DOC读取策略
 */
@Slf4j
@Component
public class DocReaderStrategy implements ReaderStrategy {

    @Override
    public boolean support(String fileType) {
        return FileType.DOC.suffix.contains(fileType);
    }

    @Override
    public List<Document> read(byte[] bytes) {
        Resource resource = new ByteArrayResource(bytes);
        return new TikaDocumentReader(resource).get();
    }
}
