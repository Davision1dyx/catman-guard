package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@Data
public class FileRecognizeVO {

    private String recognizedUrl;

    public static FileRecognizeVO build(String recognizedUrl) {
        FileRecognizeVO fileRecognizeVO = new FileRecognizeVO();
        fileRecognizeVO.setRecognizedUrl(recognizedUrl);
        return fileRecognizeVO;
    }
}
