package com.medicine.platform.identity.application;

import com.medicine.platform.identity.domain.User;

/**
 * 登录用例结果：JWT 与当前用户。
 */
public record LoginResult(String token, int expiresIn, User user) {
}
