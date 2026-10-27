package com.medicine.platform.common.security;

/**
 * 当前登录用户上下文，由 {@link JwtAuthenticationFilter} 从 JWT 解析后放入 SecurityContext。
 *
 * @param userId 用户 ID
 * @param role   角色
 * @param name   姓名
 */
public record CurrentUser(Long userId, Role role, String name) {
}
