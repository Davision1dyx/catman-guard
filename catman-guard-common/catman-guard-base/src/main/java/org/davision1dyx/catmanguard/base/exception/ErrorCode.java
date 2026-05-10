package org.davision1dyx.catmanguard.base.exception;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
public enum ErrorCode {

    SUCCESS("00000", "成功", 200),
    NO_CHAT_MODEL("00001", "没有可用的ChatModel", 500),
    NO_FILE_TYPE_SUPPORT("00002", "没有支持的文件类型", 400),
    PARAM_ERROR("00003", "参数错误", 400),
    ERROR("99999", "内部失败", 500);

    public final String code;
    public final String message;
    public final int httpCode;

    ErrorCode(String code, String message, int httpCode) {
        this.code = code;
        this.message = message;
        this.httpCode = httpCode;
    }
}
