package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 操作结果响应VO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OperationResultVO {

    /**
     * 是否操作成功
     */
    private Boolean success;
}