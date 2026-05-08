package org.davision1dyx.catmanguard.api.storage.service;

import org.davision1dyx.catmanguard.api.storage.dto.ChunkEmbedDTO;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkListDTO;
import org.davision1dyx.catmanguard.api.storage.dto.ChunkQueryDTO;
import org.davision1dyx.catmanguard.api.storage.dto.EmbedSyncDTO;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkEmbedVO;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkListVO;
import org.davision1dyx.catmanguard.api.storage.vo.ChunkQueryVO;
import org.davision1dyx.catmanguard.api.storage.vo.EmbedSyncVO;

/**
 * @author Davison
 * @date 2026-05-05
 * @description 分片服务
 */
public interface ChunkService {
    /**
     * 查询文件分片信息
     *
     * @param chunkQueryDTO 文件分片查询参数
     * @return 文件分片信息
     */
    ChunkQueryVO query(ChunkQueryDTO chunkQueryDTO);

    ChunkListVO list(ChunkListDTO chunkListDTO);

    /**
     * 文件分片嵌入
     *
     * @param chunkEmbedDTO 文件分片嵌入参数
     * @return 文件分片嵌入信息
     */
    ChunkEmbedVO embed(ChunkEmbedDTO chunkEmbedDTO);

    /**
     * 同步向量库
     *
     * @param embedSyncDTO 同步参数
     * @return 同步结果
     */
    EmbedSyncVO sync(EmbedSyncDTO embedSyncDTO);
}
