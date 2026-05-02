package org.davision1dyx.catmanguard.base.exception;

/**
 * @author Davison
 * @date 2026-04-30
 * @description
 */
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;
    private String code;
    private String message;

    public BizException(ErrorCode errorCode, String message) {
        super(message);
        this.code = errorCode.code;
        this.message = message;
    }

    public BizException(ErrorCode errorCode) {
        super(errorCode.message);
        this.code = errorCode.code;
        this.message = errorCode.message;
    }
}