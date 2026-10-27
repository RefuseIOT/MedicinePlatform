package com.medicine.platform.identity.domain;

import java.time.LocalDateTime;

import com.medicine.platform.common.security.Role;

import lombok.Getter;
import lombok.Setter;

/**
 * 用户实体，对应 sys_user 表。密码为 BCrypt 摘要，不对外暴露。
 */
@Getter
@Setter
public class User {

    private Long id;
    private String phone;
    private String password;
    private String name;
    private Role role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
