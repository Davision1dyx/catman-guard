package org.davision1dyx.catmanguard.api.storage.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Davison
 * @date 2026-06-02
 * @description 工单文件创建DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IssueFileCreateDTO {

    /**
     * 文件ID
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 文件标题
     */
    private String fileTitle;

    /**
     * 知识库ID
     */
    private String knowledgeId;

    /**
     * 文件描述
     */
    private String description;
}