package com.medicine.platform.common.exception;

import com.medicine.platform.common.api.ErrorCode;

/**
 * 业务异常：携带业务错误码与可选的补充数据，由 {@link GlobalExceptionHandler} 统一转换。
 */
public class BizException extends RuntimeException {

    private final int code;
    private final Object data;

    public BizException(ErrorCode errorCode) {
        this(errorCode, null);
    }

    public BizException(ErrorCode errorCode, Object data) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.data = data;
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
        this.data = null;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }
}
