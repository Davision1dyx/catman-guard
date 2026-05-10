package org.davision1dyx.catmanguard.storage.enums;

/**
 * @author Davison
 * @date 2026-05-08
 * @description 知识库类型
 */
public enum KnowledgeType {
    PRODUCT("product", "产品文档"),
    TECHNICAL("technical", "技术文档"),
    OPERATION("operation", "运维文档"),
    TRAINING("training", "培训资料");

    private final String code;
    private final String description;

    KnowledgeType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static KnowledgeType fromCode(String code) {
        for (KnowledgeType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        return null;
    }
}