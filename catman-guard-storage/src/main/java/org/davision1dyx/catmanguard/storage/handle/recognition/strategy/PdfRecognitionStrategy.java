package org.davision1dyx.catmanguard.storage.handle.recognition.strategy;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.base.util.FileUtil;
import org.davision1dyx.catmanguard.recognition.tool.MinerURecognizeTool;
import org.davision1dyx.catmanguard.storage.enums.FileType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
}
