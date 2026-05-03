package org.davision1dyx.catmanguard.storage.handle.recognition.strategy;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-02
 * @description MARKDOWN识别策略
 */
public class MarkdownRecognitionStrategy implements RecognitionStrategy{
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
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                // 水平线分割生成新文档
                .withHorizontalRuleCreateDocument(true)
                // 不包含代码块
                .withIncludeCodeBlock(false)
                // 不包含引用
                .withIncludeBlockquote(false)
                // 添加文件名元数据
//                .withAdditionalMetadata("filename", file.getName())
                .build();
        return new MarkdownDocumentReader(new ByteArrayResource(bytes), config).read();
    }
}
