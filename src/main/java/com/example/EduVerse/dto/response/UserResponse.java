package com.example.EduVerse.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private String fullName;
    private String avatarUrl;
    private String provider;
    private String role;
    private String rankLevel;
    private Integer contributionPoint;
    private String status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
