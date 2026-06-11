package com.example.EduVerse.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "TOKEN_REQUIRED")
    private String resetToken;

    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8, message = "PASSWORD_INVALID")
    private String newPassword;

    @NotBlank(message = "PASSWORD_REQUIRED")
    private String confirmPassword;
}
