package org.davision1dyx.catmanguard.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.davision1dyx.catmanguard.storage.model.Knowledge;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 知识库Mapper
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
}