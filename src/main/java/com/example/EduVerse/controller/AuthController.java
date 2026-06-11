package com.example.EduVerse.controller;

import com.example.EduVerse.dto.request.*;
import com.example.EduVerse.dto.response.*;
import com.example.EduVerse.service.AuthService;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ApiResponse<RegisterResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        return ApiResponse.<RegisterResponse>builder()
                .data(authService.register(registerRequest))
                .build();
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ApiResponse.<LoginResponse>builder()
                .data(authService.login(loginRequest))
                .build();
    }

    @PostMapping("/refresh")
    public ApiResponse<RefreshTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ApiResponse.<RefreshTokenResponse>builder()
                .data(authService.refreshToken(refreshTokenRequest))
                .build();
    }

    @GetMapping("/oauth2/success")
    public ApiResponse<LoginResponse> oauth2Success(@RequestParam String accessToken,
                                                    @RequestParam String refreshToken,
                                                    @RequestParam String username,
                                                    @RequestParam String role) {
        LoginResponse loginResponse = LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(username)
                .role(role)
                .build();

        return ApiResponse.<LoginResponse>builder()
                .message("Đăng nhập bằng tài khoản Google thành công!")
                .data(loginResponse)
                .build();
    }

    @PutMapping("/password/change")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        authService.changePassword(changePasswordRequest);
        return ApiResponse.<Void>builder()
                .message("Thay đổi mật khẩu tài khoản thành công!")
                .build();
    }

    @PostMapping("/password/forgot")
    public ApiResponse<Void> sendOtp(@Valid @RequestBody SendOtpRequest sendOtpRequest) {
        authService.sendOtpForgotPassword(sendOtpRequest);
        return ApiResponse.<Void>builder()
                .message("Mã xác thực OTP đã được gửi về Email của bạn.")
                .build();
    }

    @PostMapping("/password/verify")
    public ApiResponse<VerifyOtpResponse> verifyOtp(@Valid @RequestBody VerifyOtpRequest verifyOtpRequest) {
        return ApiResponse.<VerifyOtpResponse>builder()
                .message("Xác thực mã OTP thành công!")
                .data(authService.verifyOtpForgotPassword(verifyOtpRequest))
                .build();
    }

    @PostMapping("/password/reset")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {
        authService.resetPasswordWithToken(resetPasswordRequest);
        return ApiResponse.<Void>builder()
                .message("Đặt lại mật khẩu mới thành công! Bạn có thể tiến hành đăng nhập.")
                .build();
    }
}
