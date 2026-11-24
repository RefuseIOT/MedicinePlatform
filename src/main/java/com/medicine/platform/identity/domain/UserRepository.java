package com.medicine.platform.identity.domain;

import java.util.Optional;

/**
 * 用户仓储接口（端口），由 infrastructure 层实现。
 */
public interface UserRepository {

    Optional<User> findByPhone(String phone);

    Optional<User> findById(Long id);
}
