package com.example.EduVerse.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.scheduling.annotation.Async;

@Data
@Async
@NoArgsConstructor
public class UpdateUserRequest {

    @NotBlank(message = "FULLNAME_REQUIRED")
    private String fullName;

    private String avatarUrl;
}
