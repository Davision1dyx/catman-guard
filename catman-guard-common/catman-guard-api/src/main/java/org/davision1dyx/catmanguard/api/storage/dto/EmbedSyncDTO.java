package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-07
 * @description
 */
@Data
public class EmbedSyncDTO {

    private String knowledgeId;

    public static EmbedSyncDTO build(String knowledgeId) {
        EmbedSyncDTO dto = new EmbedSyncDTO();
        dto.setKnowledgeId(knowledgeId);
        return dto;
    }
}