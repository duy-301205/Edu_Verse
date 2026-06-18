package com.example.EduVerse.service;

import com.example.EduVerse.dto.request.UpdateUserRequest;
import com.example.EduVerse.dto.response.UserPublicResponse;
import com.example.EduVerse.dto.response.UserResponse;
import com.example.EduVerse.entity.User;
import com.example.EduVerse.exception.AppException;
import com.example.EduVerse.exception.ErrorCode;
import com.example.EduVerse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public UserResponse getUser() {
        User user = getCurrentUser();

        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(UpdateUserRequest updateUserRequest) {
        User user = getCurrentUser();

        if(updateUserRequest.getFullName() != null) {
            user.setFullName(updateUserRequest.getFullName());
        }

        if(updateUserRequest.getAvatarUrl() != null) {
            user.setAvatarUrl(updateUserRequest.getAvatarUrl());
        }

        user.setUpdatedAt(ZonedDateTime.now());

        User userUpdated = userRepository.save(user);

        return toUserResponse(userUpdated);
    }

    @Transactional(readOnly = true)
    public UserPublicResponse getUserPublic(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(!"ACTIVE".equals(user.getStatus().toString())) {
            throw new AppException(ErrorCode.USER_BANNED);
        }

        return toUserPublicResponse(user);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return user;
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .provider(user.getProvider().name())
                .role(user.getRole().name())
                .rankLevel(user.getRankLevel().name())
                .contributionPoint(user.getContributionPoint())
                .status(user.getStatus().name())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    private UserPublicResponse toUserPublicResponse(User user) {
        return UserPublicResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .avatarUrl(user.getAvatarUrl())
                .rankLevel(user.getRankLevel().name())
                .build();
    }

}
