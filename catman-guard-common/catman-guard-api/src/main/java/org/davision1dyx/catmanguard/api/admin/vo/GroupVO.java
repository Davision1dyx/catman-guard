package org.davision1dyx.catmanguard.api.admin.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 分组VO
 */
@Data
public class GroupVO {

    /**
     * 分组ID
     */
    private String id;

    /**
     * 分组名称
     */
    private String name;

    /**
     * 成员列表
     */
    private List<GroupMemberVO> members;

    @Data
    public static class GroupMemberVO {
        private String id;
        private String name;
    }
}