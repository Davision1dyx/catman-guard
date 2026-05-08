package org.davision1dyx.catmanguard.api.storage.vo;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-07
 * @description
 */
@Data
public class EmbedSyncVO {

    private Integer syncedCount;

    public static EmbedSyncVO build(Integer syncedCount) {
        EmbedSyncVO vo = new EmbedSyncVO();
        vo.setSyncedCount(syncedCount);
        return vo;
    }
}