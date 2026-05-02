package org.davision1dyx.catmanguard.storage.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.davision1dyx.catmanguard.datasource.entity.BaseModel;

import java.time.LocalDateTime;

/**
 * @author Davison
 * @date 2026-05-02
 * @description
 */
@Data
@TableName("file_info")
public class FileInfo extends BaseModel {

    /**
     * 文件唯一标识(UUID)
     */
    @TableField("file_id")
    private String fileId;

    /**
     * 文件名称
     */
    @TableField("file_Name")
    private String fileName;

    /**
     * 文件标题
     */
    @TableField("file_title")
    private String fileTitle;

    /**
     * 文件类型(pdf/doc/docx/txt/md等)
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件URL
     */
    @TableField("url")
    private String url;

    /**
     * 转换后的文件URL
     */
    @TableField("converted_url")
    private String convertedUrl;

    /**
     * 存储类型(local/minio)
     */
    @TableField("storage_type")
    private String storageType;

    /**
     * 状态：INIT, UPLOADED, CONVERTING, CONVERTED, CHUNKED, VECTOR_STORED
     */
    @TableField("status")
    private String status;

    /**
     * 文件扩展信息
     */
    @TableField("extension")
    private String extension;

    /**
     * 文件描述
     */
    @TableField("description")
    private String description;

    /**
     * 上传用户
     */
    @TableField("upload_user")
    private String uploadUser;

    /**
     * 过期时间
     */
    @TableField("expire_time")
    private LocalDateTime expireTime;
}