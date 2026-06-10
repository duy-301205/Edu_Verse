package com.example.EduVerse.controller;

import com.example.EduVerse.dto.request.LoginRequest;
import com.example.EduVerse.dto.request.RegisterRequest;
import com.example.EduVerse.dto.response.ApiResponse;
import com.example.EduVerse.dto.response.LoginResponse;
import com.example.EduVerse.dto.response.RegisterResponse;
import com.example.EduVerse.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
