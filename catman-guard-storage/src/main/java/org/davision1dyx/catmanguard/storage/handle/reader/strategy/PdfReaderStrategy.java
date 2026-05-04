package org.davision1dyx.catmanguard.storage.handle.reader.strategy;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.recognition.tool.MinerURecognizeTool;
import org.davision1dyx.catmanguard.storage.enums.FileType;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-02
 * @description PDF读取策略
 */
@Slf4j
@Component
public class PdfReaderStrategy implements ReaderStrategy {

    private final MinerURecognizeTool minerURecognizeTool;

    public PdfReaderStrategy(MinerURecognizeTool minerURecognizeTool) {
        this.minerURecognizeTool = minerURecognizeTool;
    }

    @Override
    public boolean support(String fileType) {
        return FileType.PDF.suffix.contains(fileType);
    }

    @Override
    public List<Document> read(byte[] bytes) {
        PdfDocumentReaderConfig config = PdfDocumentReaderConfig.builder()
                .withPageTopMargin(50)         // 忽略顶部50个单位的页眉
                .withPageBottomMargin(50)      // 忽略底部50个单位的页脚
                .withPagesPerDocument(1)       // 每一页作为一个 Document
                .withPageExtractedTextFormatter(new ExtractedTextFormatter.Builder()
                        .withNumberOfTopTextLinesToDelete(0) // 每页再额外删掉前0行
                        .build())
                .build();

//        return new ParagraphPdfDocumentReader(new FileSystemResource(file), config).read();
        return new PagePdfDocumentReader(new ByteArrayResource(bytes), config).read();
    }
}
