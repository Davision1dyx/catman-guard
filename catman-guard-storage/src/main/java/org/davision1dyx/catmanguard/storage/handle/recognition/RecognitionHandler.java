package org.davision1dyx.catmanguard.storage.handle.recognition;

import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.base.util.FileUtil;
import org.davision1dyx.catmanguard.storage.handle.recognition.strategy.RecognitionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
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
                .filter(strategy -> {
                    String fileType = FileUtil.getFileType(file.getOriginalFilename());
                    return strategy.support(fileType);
                })
                .findFirst().orElseThrow(() -> new BizException(ErrorCode.NO_FILE_TYPE_SUPPORT));
        return recognitionStrategy.recognize(file);
    }

    public String handle(InputStream inputStream, String fileName) throws IOException {
        RecognitionStrategy recognitionStrategy = recognitionStrategies.stream()
                .filter(strategy -> {
                    String fileType = FileUtil.getFileType(fileName);
                    return strategy.support(fileType);
                })
                .findFirst().orElseThrow(() -> new BizException(ErrorCode.NO_FILE_TYPE_SUPPORT));
        return recognitionStrategy.recognize(inputStream, fileName);
    }
}
