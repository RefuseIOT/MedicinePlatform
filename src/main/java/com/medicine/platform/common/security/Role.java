package com.medicine.platform.common.security;

/**
 * 系统角色。四类角色见《需求规格说明书》§2.1。
 * <p>放在 common 而非 identity，是因为 JWT、认证过滤器、当前用户上下文等公共内核都要使用它，
 * 而 common 不能反向依赖 identity。</p>
 */
public enum Role {
    PATIENT,
    DOCTOR,
    PHARMACIST,
    ADMIN
}
