package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Davison
 * @date 2026-05-05
 * @description
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetrievalDTO {
    private String query;
    private Integer topN;
    private Double similarity;
}
