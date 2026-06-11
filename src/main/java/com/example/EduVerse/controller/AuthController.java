package com.example.EduVerse.controller;

import com.example.EduVerse.dto.request.LoginRequest;
import com.example.EduVerse.dto.request.RefreshTokenRequest;
import com.example.EduVerse.dto.request.RegisterRequest;
import com.example.EduVerse.dto.response.ApiResponse;
import com.example.EduVerse.dto.response.LoginResponse;
import com.example.EduVerse.dto.response.RefreshTokenResponse;
import com.example.EduVerse.dto.response.RegisterResponse;
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
}
