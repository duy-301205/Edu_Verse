package com.example.EduVerse.service;

import com.example.EduVerse.dto.request.LoginRequest;
import com.example.EduVerse.dto.request.RegisterRequest;
import com.example.EduVerse.dto.response.LoginResponse;
import com.example.EduVerse.dto.response.RegisterResponse;
import com.example.EduVerse.entity.User;
import com.example.EduVerse.entity.Wallet;
import com.example.EduVerse.enums.UserStatus;
import com.example.EduVerse.exception.AppException;
import com.example.EduVerse.exception.ErrorCode;
import com.example.EduVerse.repository.UserRepository;
import com.example.EduVerse.repository.WalletRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilsService jwtUtilsService;

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

}
