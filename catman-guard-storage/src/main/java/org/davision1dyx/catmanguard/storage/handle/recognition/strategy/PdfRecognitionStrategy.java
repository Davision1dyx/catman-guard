package org.davision1dyx.catmanguard.storage.handle.recognition.strategy;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.base.util.FileUtil;
import org.davision1dyx.catmanguard.recognition.tool.MinerURecognizeTool;
import org.davision1dyx.catmanguard.storage.enums.FileType;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author Davison
 * @date 2026-05-02
 * @description PDF OCR识别策略
 */
@Slf4j
@Component
public class PdfRecognitionStrategy implements RecognitionStrategy{

    private final MinerURecognizeTool minerURecognizeTool;
    private final static String unzipTmpPath = "/home/catman/tmp";

    public PdfRecognitionStrategy(MinerURecognizeTool minerURecognizeTool) {
        this.minerURecognizeTool = minerURecognizeTool;
    }

    @Override
    public boolean support(String fileType) {
        return FileType.PDF.suffix.contains(fileType);
    }

    @Override
    public String recognize(MultipartFile file) throws IOException {
        InputStream inputStream = file.getInputStream();

        // 1.OCR识别，得到zip
        byte[] zipBytes = minerURecognizeTool.parseDocumentToZip(file.getOriginalFilename(), inputStream);

        // 2. 解压zip
        String uniqueId = UUID.randomUUID().toString();
        String zipFilePath = unzipTmpPath + File.separator + uniqueId + ".zip";
        String extractDir = unzipTmpPath + File.separator + uniqueId + "_extracted";

        FileUtil.writeBytes(zipBytes, zipFilePath);
        log.info("Zip file saved to: {}", zipFilePath);
        FileUtil.unzip(zipFilePath, extractDir);
        log.info("Zip file extracted to: {}", extractDir);

        return extractDir;
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
