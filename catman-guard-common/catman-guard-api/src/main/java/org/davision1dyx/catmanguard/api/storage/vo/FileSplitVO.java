package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@Data
public class FileSplitVO {
    private Integer chunkSize;

    public static FileSplitVO build(Integer chunkSize) {
        FileSplitVO fileSplitVO = new FileSplitVO();
        fileSplitVO.setChunkSize(chunkSize);
        return fileSplitVO;
    }
}
