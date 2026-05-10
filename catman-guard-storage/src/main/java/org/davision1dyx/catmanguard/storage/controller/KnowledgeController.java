package org.davision1dyx.catmanguard.storage.controller;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.api.storage.dto.CreateKnowledgeDTO;
import org.davision1dyx.catmanguard.api.storage.dto.KnowledgeListDTO;
import org.davision1dyx.catmanguard.api.storage.dto.UpdateKnowledgeDTO;
import org.davision1dyx.catmanguard.api.storage.service.KnowledgeService;
import org.davision1dyx.catmanguard.api.storage.vo.CreateKnowledgeVO;
import org.davision1dyx.catmanguard.api.storage.vo.KnowledgeVO;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.davision1dyx.catmanguard.base.exception.ErrorCode;
import org.davision1dyx.catmanguard.storage.enums.KnowledgeType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 知识库管理接口
 */
@Slf4j
@RestController
@RequestMapping("/processing/catman/storage/knowledge")
public class KnowledgeController {

    private final KnowledgeService knowledgeService;

    public KnowledgeController(KnowledgeService knowledgeService) {
        this.knowledgeService = knowledgeService;
    }

    /**
     * 获取知识库列表
     */
    @PostMapping("/list")
    public List<KnowledgeVO> list(@RequestBody KnowledgeListDTO dto) {
        log.info("[POST] /processing/catman/storage/knowledge/list, body: {}", dto);
        return knowledgeService.listKnowledge(dto);
    }

    /**
     * 创建知识库
     */
    @PostMapping("/create")
    public CreateKnowledgeVO create(@RequestBody CreateKnowledgeDTO dto) {
        if (dto.getType() == null) {
            throw new IllegalArgumentException("知识库类型不能为空");
        }
        if (KnowledgeType.fromCode(dto.getType()) == null) {
            throw new IllegalArgumentException("非法的知识库类型: " + dto.getType());
        }
        log.info("[POST] /processing/catman/storage/knowledge/create, body: {}", dto);
        return knowledgeService.createKnowledge(dto);
    }

    /**
     * 更新知识库
     */
    @PutMapping("/{id}")
    public KnowledgeVO update(@PathVariable String id, @RequestBody UpdateKnowledgeDTO dto) {
        if (dto.getType() != null && KnowledgeType.fromCode(dto.getType()) == null) {
            throw new BizException(ErrorCode.PARAM_ERROR, "非法的知识库类型: " + dto.getType());
        }
        log.info("[PUT] /processing/catman/storage/knowledge/{}", id);
        return knowledgeService.updateKnowledge(id, dto);
    }

    /**
     * 删除知识库
     */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        log.info("[DELETE] /processing/catman/storage/knowledge/{}", id);
        knowledgeService.deleteKnowledge(id);
    }
}