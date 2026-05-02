package org.davision1dyx.catmanguard.base.exception;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
public enum ErrorCode {

    SUCCESS("00000", "成功"),
    NO_CHAT_MODEL("00001", "没有可用的ChatModel"),
    NO_FILE_TYPE_SUPPORT("00002", "没有支持的文件类型"),

    ERROR("99999", "内部失败");

    public final String code;
    public final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
