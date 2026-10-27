package com.medicine.platform.identity.interfaces.dto;

/**
 * 登录响应体：token、有效期（秒）与用户信息。
 */
public record LoginResponse(String token, int expiresIn, UserVO user) {
}
