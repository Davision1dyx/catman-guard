package org.davision1dyx.catmanguard.datasource.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDateTime;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
public class MetaDataHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        this.setFieldValByNameIfNull("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByNameIfNull("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("deleted", 0, metaObject);
        this.setFieldValByName("lockVersion", 0, metaObject);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }

    public void setFieldValByNameIfNull(String fieldName, Object fieldValue, MetaObject metaObject) {
        if (metaObject.getValue(fieldName) == null) {
            this.setFieldValByName(fieldName, fieldValue, metaObject);
        }
    }

}
