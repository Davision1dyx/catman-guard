package org.davision1dyx.catmanguard.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.davision1dyx.catmanguard.admin.model.Group;
import org.davision1dyx.catmanguard.api.admin.dto.CreateGroupDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateGroupDTO;
import org.davision1dyx.catmanguard.api.admin.vo.GroupVO;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 分组Service接口
 */
public interface GroupService extends IService<Group> {

    /**
     * 获取分组列表（含成员）
     * @return 分组列表
     */
    List<GroupVO> listGroupWithMembers();

    /**
     * 创建分组
     * @param dto 创建参数
     * @return 创建结果
     */
    GroupVO createGroup(CreateGroupDTO dto);

    /**
     * 更新分组
     * @param groupId 分组ID
     * @param dto 更新参数
     * @return 更新结果
     */
    GroupVO updateGroup(String groupId, UpdateGroupDTO dto);

    /**
     * 删除分组
     * @param groupId 分组ID
     * @return 是否删除成功
     */
    boolean deleteGroup(String groupId);
}
