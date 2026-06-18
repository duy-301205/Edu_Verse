package com.example.EduVerse.controller;

import com.example.EduVerse.dto.request.UpdateUserRequest;
import com.example.EduVerse.dto.response.ApiResponse;
import com.example.EduVerse.dto.response.UserPublicResponse;
import com.example.EduVerse.dto.response.UserResponse;
import com.example.EduVerse.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserResponse> getUser() {
        return ApiResponse.<UserResponse>builder()
                .data(userService.getUser())
                .build();
    }

    @PutMapping("/update")
    public ApiResponse<UserResponse> updateUser(@Valid @RequestBody UpdateUserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .data(userService.updateUser(request))
                .build();
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<UserPublicResponse> getUserPublic(@PathVariable Long userId) {
        return ApiResponse.<UserPublicResponse>builder()
                .data(userService.getUserPublic(userId))
                .build();
    }
}

