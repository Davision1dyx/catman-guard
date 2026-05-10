package org.davision1dyx.catmanguard.storage.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.CreateKnowledgeDTO;
import org.davision1dyx.catmanguard.api.storage.dto.KnowledgeListDTO;
import org.davision1dyx.catmanguard.api.storage.dto.UpdateKnowledgeDTO;
import org.davision1dyx.catmanguard.api.storage.service.KnowledgeService;
import org.davision1dyx.catmanguard.api.storage.vo.CreateKnowledgeVO;
import org.davision1dyx.catmanguard.api.storage.vo.KnowledgeVO;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.storage.convertor.KnowledgeConvertor;
import org.davision1dyx.catmanguard.storage.mapper.KnowledgeMapper;
import org.davision1dyx.catmanguard.storage.model.Knowledge;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 知识库Service实现
 */
@Slf4j
@Service
public class KnowledgeServiceImpl extends ServiceImpl<KnowledgeMapper, Knowledge> implements KnowledgeService {

    @Override
    public List<KnowledgeVO> listKnowledge(KnowledgeListDTO dto) {
        log.info("获取知识库列表, search: {}", dto.getSearch());

        LambdaQueryWrapper<Knowledge> queryWrapper = new LambdaQueryWrapper<>();
        if (dto.getSearch() != null && !dto.getSearch().isEmpty()) {
            queryWrapper.like(Knowledge::getName, dto.getSearch())
                    .or().like(Knowledge::getDescription, dto.getSearch());
        }

        List<Knowledge> knowledgeList = list(queryWrapper);

        return knowledgeList.stream()
                .map(KnowledgeConvertor.INSTANCE::mapToVO)
                .toList();
    }

    @Override
    public CreateKnowledgeVO createKnowledge(CreateKnowledgeDTO dto) {
        log.info("创建知识库, name: {}, description: {}, type: {}", dto.getName(), dto.getDescription(), dto.getType());

        Knowledge knowledge = new Knowledge();
        knowledge.setKnowledgeId("kb-" + java.util.UUID.randomUUID().toString().substring(0, 8));
        knowledge.setName(dto.getName());
        knowledge.setDescription(dto.getDescription());
        knowledge.setType(dto.getType());
        knowledge.setFileCount(0);
        knowledge.setChunkCount(0);
        knowledge.setStatus("ACTIVE");

        save(knowledge);

        return CreateKnowledgeVO.builder()
                .knowledgeId(knowledge.getKnowledgeId())
                .build();
    }

    @Override
    public KnowledgeVO updateKnowledge(String knowledgeId, UpdateKnowledgeDTO dto) {
        log.info("更新知识库, knowledgeId: {}, name: {}, description: {}, type: {}",
                knowledgeId, dto.getName(), dto.getDescription(), dto.getType());

        Knowledge knowledge = getOne(new LambdaQueryWrapper<Knowledge>()
                .eq(Knowledge::getKnowledgeId, knowledgeId));

        if (knowledge == null) {
            throw new BizException(ErrorCode.ERROR, "知识库不存在");
        }

        if (dto.getName() != null) {
            knowledge.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            knowledge.setDescription(dto.getDescription());
        }
        if (dto.getType() != null) {
            knowledge.setType(dto.getType());
        }

        updateById(knowledge);

        return KnowledgeConvertor.INSTANCE.mapToVO(knowledge);
    }

    @Override
    public void deleteKnowledge(String knowledgeId) {
        log.info("删除知识库, knowledgeId: {}", knowledgeId);

        boolean removed = remove(new LambdaQueryWrapper<Knowledge>()
                .eq(Knowledge::getKnowledgeId, knowledgeId));

        if (!removed) {
            throw new BizException(ErrorCode.ERROR, "知识库不存在");
        }
    }
}