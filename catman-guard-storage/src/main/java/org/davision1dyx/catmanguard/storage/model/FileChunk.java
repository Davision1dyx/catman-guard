package org.davision1dyx.catmanguard.storage.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.davision1dyx.catmanguard.datasource.entity.BaseModel;

/**
 * @author Davison
 * @date 2026-05-04
 * @description 文件分片
 */
@Data
@TableName("file_chunk")
public class FileChunk extends BaseModel {

    /**
     * 分片唯一标识(UUID)
     */
    @TableField("chunk_id")
    private String chunkId;

    /**
     * 文件ID
     */
    @TableField("file_id")
    private String fileId;

    /**
     * 分片序号
     */
    @TableField("chunk_index")
    private Integer chunkIndex;

    /**
     * 分片内容
     */
    @TableField("content")
    private String content;

    /**
     * 元数据
     */
    @TableField("meta_data")
    private String metaData;
}
