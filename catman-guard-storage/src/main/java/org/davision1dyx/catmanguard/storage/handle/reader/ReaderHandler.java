package org.davision1dyx.catmanguard.storage.handle.reader;

import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.storage.handle.reader.strategy.ReaderStrategy;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-03
 * @description 文件读取处理器
 */
@Component
public class ReaderHandler {

    private final List<ReaderStrategy> readerStrategies;

    public ReaderHandler(List<ReaderStrategy> readerStrategies) {
        this.readerStrategies = readerStrategies;
    }

    public List<Document> handle(byte[] bytes, String fileType) {
        ReaderStrategy recognitionStrategy = readerStrategies.stream()
                .filter(strategy -> strategy.support(fileType))
                .findFirst().orElseThrow(() -> new BizException(ErrorCode.NO_FILE_TYPE_SUPPORT));
        return recognitionStrategy.read(bytes);
    }
}
