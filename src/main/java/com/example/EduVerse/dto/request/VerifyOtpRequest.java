package com.example.EduVerse.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VerifyOtpRequest {
    @NotBlank(message = "EMAIL_REQUIRED")
    @Email(message = "EMAIL_INVALID")
    private String email;

    @NotBlank(message = "OTP_REQUIRED")
    private String otp;
}
