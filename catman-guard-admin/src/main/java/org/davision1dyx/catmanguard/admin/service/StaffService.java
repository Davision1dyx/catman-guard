package org.davision1dyx.catmanguard.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.davision1dyx.catmanguard.admin.model.Staff;
import org.davision1dyx.catmanguard.api.admin.dto.CreateStaffDTO;
import org.davision1dyx.catmanguard.api.admin.dto.StaffListDTO;
import org.davision1dyx.catmanguard.api.admin.dto.UpdateStaffDTO;
import org.davision1dyx.catmanguard.api.admin.vo.CreateStaffVO;
import org.davision1dyx.catmanguard.api.admin.vo.StaffListVO;
import org.davision1dyx.catmanguard.api.admin.vo.UpdateStaffVO;

/**
 * @author Davison
 * @date 2026-05-06
 * @description 值班人员Service接口
 */
public interface StaffService extends IService<Staff> {

    /**
     * 获取值班人员列表
     * @param dto 查询条件
     * @return 值班人员列表
     */
    StaffListVO listStaff(StaffListDTO dto);

    /**
     * 创建值班人员
     * @param dto 创建参数
     * @return 创建结果
     */
    CreateStaffVO createStaff(CreateStaffDTO dto);

    /**
     * 更新值班人员
     * @param staffId 人员ID
     * @param dto 更新参数
     * @return 更新结果
     */
    UpdateStaffVO updateStaff(String staffId, UpdateStaffDTO dto);

    /**
     * 删除值班人员
     * @param staffId 人员ID
     * @return 是否删除成功
     */
    boolean deleteStaff(String staffId);
}
