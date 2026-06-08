package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Davison
 * @date 2026-06-02
 * @description 工单向量化结果VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueVectorizeVO {

    /**
     * 生成的chunk数量
     */
    private Integer chunkSize;

    /**
     * 关联的文件ID
     */
    private String fileId;
}