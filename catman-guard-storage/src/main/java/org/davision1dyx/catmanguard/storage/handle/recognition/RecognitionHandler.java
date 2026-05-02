package org.davision1dyx.catmanguard.storage.handle.recognition;

import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.storage.handle.recognition.strategy.RecognitionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * @author Davison
 * @date 2026-05-02
 * @description 文件识别处理器
 */
@Component
public class RecognitionHandler {

    private final List<RecognitionStrategy> recognitionStrategies;

    public RecognitionHandler(List<RecognitionStrategy> recognitionStrategies) {
        this.recognitionStrategies = recognitionStrategies;
    }

    public String handle(MultipartFile file) throws IOException {
        RecognitionStrategy recognitionStrategy = recognitionStrategies.stream()
                .filter(strategy -> strategy.support(file))
                .findFirst().orElseThrow(() -> new BizException(ErrorCode.NO_FILE_TYPE_SUPPORT));
        return recognitionStrategy.recognize(file);
    }
}
