package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 热门问题VO
 */
@Data
public class HotQuestionVO {

    /**
     * 问题
     */
    private String question;

    /**
     * 数量
     */
    private Integer count;

    /**
     * 已解决数
     */
    private Integer resolved;

    /**
     * 解决率
     */
    private Integer resolveRate;
}