package com.medicine.platform.identity.infrastructure;

import com.medicine.platform.identity.domain.User;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * sys_user 的 MyBatis Mapper。列名 snake_case 与字段 camelCase 由 mybatis 配置自动映射。
 */
@Mapper
public interface UserMapper {

    @Select("SELECT id, phone, password, name, role, status, created_at, updated_at FROM sys_user WHERE phone = #{phone} LIMIT 1")
    User findByPhone(String phone);

    @Select("SELECT id, phone, password, name, role, status, created_at, updated_at FROM sys_user WHERE id = #{id} LIMIT 1")
    User findById(Long id);
}
