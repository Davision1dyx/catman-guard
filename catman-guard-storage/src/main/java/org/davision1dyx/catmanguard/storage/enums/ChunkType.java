package org.davision1dyx.catmanguard.storage.enums;

/**
 * @author Davison
 * @date 2026-05-30
 * @description 分片类型
 */
public enum ChunkType {
    /**
     * 固定长度
     */
    FIXED_LENGTH("fixed_length"),
    /**
     * 父子
     */
    PARENT_CHILD("parent_child"),
    /**
     * 语义理解
     */
    SEMANTIC("semantic"),
    ;

    ChunkType(String value) {
        this.value = value;
    }

    public final String value;
}
