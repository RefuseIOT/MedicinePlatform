package com.medicine.platform.identity.application;

import com.medicine.platform.common.api.ErrorCode;
import com.medicine.platform.common.exception.BizException;
import com.medicine.platform.common.security.JwtTokenProvider;
import com.medicine.platform.identity.domain.User;
import com.medicine.platform.identity.domain.UserRepository;
import com.medicine.platform.identity.domain.UserStatus;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 认证应用服务：登录与当前用户查询。
 */
@Service
public class AuthAppService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthAppService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public LoginResult login(String phone, String password) {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new BizException(ErrorCode.UNAUTHORIZED));
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new BizException(ErrorCode.FORBIDDEN);
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BizException(ErrorCode.UNAUTHORIZED);
        }
        String token = jwtTokenProvider.generateToken(user.getId(), user.getRole(), user.getName());
        return new LoginResult(token, jwtTokenProvider.getExpiresInSeconds(), user);
    }

    public User me(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BizException(ErrorCode.UNAUTHORIZED));
    }
}
