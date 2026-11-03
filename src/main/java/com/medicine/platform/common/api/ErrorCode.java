package com.medicine.platform.common.api;

/**
 * 业务错误码。分段约定见《系统设计规格说明书》§5.4：
 * 0 成功；400xx 参数；401xx 认证；403xx 授权；404xx 资源；409xx 冲突；500xx 系统。
 */
public enum ErrorCode {

    SUCCESS(0, "ok"),
    BAD_REQUEST(40001, "参数校验失败"),
    INVALID_ARGUMENT(40002, "业务参数不合法"),
    UNAUTHORIZED(40101, "未登录或登录已过期"),
    FORBIDDEN(40301, "无权限访问"),
    NOT_FOUND(40401, "资源不存在"),
    CONFLICT(40901, "资源状态已变化，请刷新后重试"),
    VERSION_CONFLICT(40902, "版本冲突，请刷新后重试"),
    DUPLICATE_OPERATION(40903, "操作已处理，请勿重复提交"),
    INTERNAL_ERROR(50000, "服务器内部错误");

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
