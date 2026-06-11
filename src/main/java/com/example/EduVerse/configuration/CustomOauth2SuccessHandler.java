package com.example.EduVerse.configuration;

import com.example.EduVerse.entity.User;
import com.example.EduVerse.entity.Wallet;
import com.example.EduVerse.enums.AuthProvider;
import com.example.EduVerse.repository.UserRepository;
import com.example.EduVerse.repository.WalletRepository;
import com.example.EduVerse.service.JwtUtilsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomOauth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;
    private final JwtUtilsService jwtUtilsService;

    private static final String FRONTEND_REDIRECT_URL = "http://localhost:8088/api/auth/oauth2/success";

    @Override
    @Transactional
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String avatarUrl = oAuth2User.getAttribute("picture");

        if (email == null || email.isBlank()) {
            log.error("Không lấy được email từ Google OAuth2");
            throw new RuntimeException("Không lấy được email từ Google OAuth2");
        }

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isEmpty()) {
            String username = generateUsernameFromEmail(email);
            user = createGoogleUser(username, email, name, avatarUrl);
            log.info("Khởi tạo tài khoản và ví nội bộ thành công cho user Google: {}", email);
        } else {
            user = userOptional.get();

            if (avatarUrl != null && !avatarUrl.isBlank()) {
                user.setAvatarUrl(avatarUrl);
                user = userRepository.save(user);
            }

            log.info("Người dùng Google đăng nhập lại và đồng bộ avatar: {}", email);
        }

        String accessToken = jwtUtilsService.generateAccessToken(user);
        String refreshToken = jwtUtilsService.generateRefreshToken(user);
        String username = user.getUsername();
        String role = user.getRole().name();

        String targetUrl = UriComponentsBuilder.fromUriString(FRONTEND_REDIRECT_URL)
                .queryParam("accessToken", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam("username", username)
                .queryParam("role", role)
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    private User createGoogleUser(String username, String email, String name, String avatarUrl) {
        User user = User.builder()
                .username(username)
                .email(email)
                .fullName(name)
                .avatarUrl(avatarUrl)
                .provider(AuthProvider.GOOGLE)
                .passwordHash(null)
                .build();

        User savedUser = userRepository.save(user);

        Wallet wallet = Wallet.builder()
                .user(savedUser)
                .coinBalance(0)
                .vndBalance(BigDecimal.ZERO)
                .build();

        walletRepository.save(wallet);

        return savedUser;
    }

    private String generateUsernameFromEmail(String email) {
        String baseUsername = email.substring(0, email.indexOf("@"));

        baseUsername = baseUsername.replaceAll("[^a-zA-Z0-9]", "");

        String finalName = baseUsername;
        Random random = new Random();

        while (userRepository.existsByUsername(finalName)) {
            int randomNumber = 100 + random.nextInt(900);
            finalName = baseUsername + "_" + randomNumber;
        }

        return finalName;
    }
}