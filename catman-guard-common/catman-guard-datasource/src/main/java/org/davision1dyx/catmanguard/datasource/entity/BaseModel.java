package org.davision1dyx.catmanguard.datasource.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.time.LocalDateTime;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
public class BaseModel {
   /**
    * 主键
    */
   @TableId(type = IdType.AUTO)
   private Long id;
   /**
    * 创建时间
    */
   @TableField(fill = FieldFill.INSERT)
   private LocalDateTime createTime;
   /**
    * 更新时间
    */
   @TableField(fill = FieldFill.INSERT_UPDATE)
   private LocalDateTime updateTime;
   /**
    * 删除标志
    */
   @TableLogic
   @TableField(fill = FieldFill.INSERT)
   private Integer deleteFlag;
   /**
    * 乐观锁版本号
    */
   @Version
   @TableField(fill = FieldFill.INSERT)
   private Integer lockVersion;

   public Long getId() {
      return id;
   }

   public void setId(Long id) {
      this.id = id;
   }

   public LocalDateTime getCreateTime() {
      return createTime;
   }

   public void setCreateTime(LocalDateTime createTime) {
      this.createTime = createTime;
   }

   public LocalDateTime getUpdateTime() {
      return updateTime;
   }

   public void setUpdateTime(LocalDateTime updateTime) {
      this.updateTime = updateTime;
   }

   public Integer getDeleteFlag() {
      return deleteFlag;
   }

   public void setDeleteFlag(Integer deleteFlag) {
      this.deleteFlag = deleteFlag;
   }

   public Integer getLockVersion() {
      return lockVersion;
   }

   public void setLockVersion(Integer lockVersion) {
      this.lockVersion = lockVersion;
   }
}
