package org.davision1dyx.catmanguard.api.admin.dto;

import lombok.Data;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 值班人员列表查询请求DTO
 */
@Data
public class StaffListDTO {

    /**
     * 搜索关键词（姓名或邮箱）
     */
    private String search;
}
