package com.medicine.platform.identity.interfaces.dto;

import com.medicine.platform.common.security.Role;
import com.medicine.platform.identity.domain.User;
import com.medicine.platform.identity.domain.UserStatus;

/**
 * 用户视图对象：手机号脱敏，不暴露密码摘要等敏感字段。
 */
public record UserVO(Long id, String name, Role role, String phoneMasked, UserStatus status) {

    public static UserVO from(User user) {
        return new UserVO(
                user.getId(),
                user.getName(),
                user.getRole(),
                maskPhone(user.getPhone()),
                user.getStatus());
    }

    private static String maskPhone(String phone) {
        if (phone == null || phone.length() < 7) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4);
    }
}
