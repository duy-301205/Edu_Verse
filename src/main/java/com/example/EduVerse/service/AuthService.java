package com.example.EduVerse.service;

import com.example.EduVerse.dto.request.*;
import com.example.EduVerse.dto.response.LoginResponse;
import com.example.EduVerse.dto.response.RefreshTokenResponse;
import com.example.EduVerse.dto.response.RegisterResponse;
import com.example.EduVerse.dto.response.VerifyOtpResponse;
import com.example.EduVerse.entity.User;
import com.example.EduVerse.entity.Wallet;
import com.example.EduVerse.enums.UserStatus;
import com.example.EduVerse.exception.AppException;
import com.example.EduVerse.exception.ErrorCode;
import com.example.EduVerse.repository.UserRepository;
import com.example.EduVerse.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilsService jwtUtilsService;
    private final StringRedisTemplate redisTemplate;
    private final EmailService emailService;

    private static final String OTP_PREFIX = "otp:";
    private static final String RESET_TOKEN_PREFIX = "reset_token:";

    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if(userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        if(userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .build();

        User savedUser = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .vndBalance(BigDecimal.ZERO)
                .coinBalance(0)
                .build();

        walletRepository.save(wallet);

        log.info("Sinh viên mới '{}' đăng ký tài khoản thành công kèm ví ID: {}", savedUser.getUsername(), wallet.getId());

        return RegisterResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .fullName(savedUser.getFullName())
                .role(savedUser.getRole().name())
                .rankLevel(savedUser.getRankLevel().name())
                .createdAt(savedUser.getCreatedAt())
                .build();
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(user.getStatus() == UserStatus.BANNED) {
            throw new AppException(ErrorCode.USER_BANNED);
        }

        if(!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String accessToken = jwtUtilsService.generateAccessToken(user);
        String refreshToken = jwtUtilsService.generateRefreshToken(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .username(user.getUsername())
                .role(user.getRole().name())
                .build();
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        String oldRefreshToken = request.getRefreshToken();

        if(jwtUtilsService.isTokenExpired(oldRefreshToken)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        String username = jwtUtilsService.extractUsername(oldRefreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(user.getStatus() != UserStatus.ACTIVE) {
            throw new AppException(ErrorCode.USER_BANNED);
        }

        String newAccessToken = jwtUtilsService.generateAccessToken(user);
        String newRefreshToken = jwtUtilsService.generateRefreshToken(user);

        return RefreshTokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.OLD_PASSWORD_INCORRECT);
        }

        if(!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        log.info("User đã thay đổi mật khẩu thành công");
    }

    public void sendOtpForgotPassword(SendOtpRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        Random rand = new Random();
        String otp = String.format("%06d", rand.nextInt(1000000));
        String redisKey = OTP_PREFIX + request.getEmail();

        // Day OTP len
        redisTemplate.opsForValue().set(redisKey, otp, 5, TimeUnit.MINUTES);

        emailService.sendOtpEmailViaBrevo(request.getEmail(), user.getUsername(), otp);
    }

    public VerifyOtpResponse verifyOtpForgotPassword(VerifyOtpRequest request) {
        String redisOtpKey = OTP_PREFIX + request.getEmail();
        String savedOtp = redisTemplate.opsForValue().get(redisOtpKey);

        if(savedOtp == null || !savedOtp.equals(request.getOtp())) {
            throw new AppException(ErrorCode.INVALID_OR_EXPIRED_OTP);
        }

        String resetToken = UUID.randomUUID().toString();
        String redisTokenKey = RESET_TOKEN_PREFIX + request.getEmail();

        redisTemplate.opsForValue().set(redisTokenKey, resetToken, 5, TimeUnit.MINUTES);

        redisTemplate.delete(redisOtpKey);

        return VerifyOtpResponse.builder()
                .resetToken(resetToken)
                .build();
    }

    public void resetPasswordWithToken(ResetPasswordRequest request) {
        String redisTokenKey = RESET_TOKEN_PREFIX + request.getEmail();
        String savedToken = redisTemplate.opsForValue().get(redisTokenKey);

        if(savedToken == null || !savedToken.equals(request.getResetToken())) {
            throw new AppException(ErrorCode.INVALID_OR_EXPIRED_TOKEN);
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if(!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        redisTemplate.delete(redisTokenKey);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
    }
}
