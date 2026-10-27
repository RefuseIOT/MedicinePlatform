package com.medicine.platform.identity.interfaces;

import com.medicine.platform.common.api.ApiResponse;
import com.medicine.platform.common.security.CurrentUser;
import com.medicine.platform.identity.application.AuthAppService;
import com.medicine.platform.identity.application.LoginResult;
import com.medicine.platform.identity.interfaces.dto.LoginRequest;
import com.medicine.platform.identity.interfaces.dto.LoginResponse;
import com.medicine.platform.identity.interfaces.dto.UserVO;

import jakarta.validation.Valid;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：登录与当前用户。
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthAppService authAppService;

    public AuthController(AuthAppService authAppService) {
        this.authAppService = authAppService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResult result = authAppService.login(request.phone(), request.password());
        return ApiResponse.ok(new LoginResponse(result.token(), result.expiresIn(), UserVO.from(result.user())));
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me(@AuthenticationPrincipal CurrentUser currentUser) {
        return ApiResponse.ok(UserVO.from(authAppService.me(currentUser.userId())));
    }
}
