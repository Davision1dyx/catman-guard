package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 工单详情响应VO
 */
@Data
public class IssueDetailVO extends IssueVO {

    /**
     * 附件列表
     */
    private List<?> attachments;

    /**
     * 回复列表
     */
    private List<?> replies;
}