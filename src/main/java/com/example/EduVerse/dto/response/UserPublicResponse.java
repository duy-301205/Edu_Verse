package com.example.EduVerse.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserPublicResponse {

    private Long id;
    private String username;
    private String fullName;
    private String avatarUrl;
    private String rankLevel;
}
