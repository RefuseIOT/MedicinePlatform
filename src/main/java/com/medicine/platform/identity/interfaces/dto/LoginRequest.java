package com.medicine.platform.identity.interfaces.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 登录请求体。
 */
public record LoginRequest(
        @NotBlank(message = "手机号不能为空") String phone,
        @NotBlank(message = "密码不能为空") String password) {
}
