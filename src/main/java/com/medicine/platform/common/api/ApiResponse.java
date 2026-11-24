package com.medicine.platform.common.api;

import java.util.UUID;

/**
 * 统一响应体。结构约定见《系统设计规格说明书》§5.3。
 *
 * @param code    业务码，0 表示成功，非 0 为业务错误码
 * @param message 给用户看的中文提示，不暴露内部异常
 * @param data    业务数据；失败时可为 null
 * @param traceId 请求追踪号，用于关联后端日志
 */
public record ApiResponse<T>(int code, String message, T data, String traceId) {

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(ErrorCode.SUCCESS.getCode(), ErrorCode.SUCCESS.getMessage(), data, newTraceId());
    }

    public static ApiResponse<Void> ok() {
        return ok(null);
    }

    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null, newTraceId());
    }

    public static <T> ApiResponse<T> error(int code, String message, T data) {
        return new ApiResponse<>(code, message, data, newTraceId());
    }

    private static String newTraceId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
