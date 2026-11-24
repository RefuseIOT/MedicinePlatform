package com.medicine.platform.identity.infrastructure;

import java.util.Optional;

import com.medicine.platform.identity.domain.User;
import com.medicine.platform.identity.domain.UserRepository;

import org.springframework.stereotype.Repository;

/**
 * 用户仓储实现：委托 MyBatis Mapper 访问 MySQL。
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserMapper userMapper;

    public UserRepositoryImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findByPhone(String phone) {
        return Optional.ofNullable(userMapper.findByPhone(phone));
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(userMapper.findById(id));
    }
}
