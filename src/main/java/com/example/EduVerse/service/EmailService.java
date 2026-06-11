package com.example.EduVerse.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    private final MailSender mailSender;
    private static final String mailFrom = "hduy9863@gmail.com";

    @Async
    public void sendOtpEmailViaBrevo(String toEmail, String username, String otp) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(mailFrom);
            message.setTo(toEmail);
            message.setSubject("[EduVerse] Mã OTP Xác Nhận Khôi Phục Mật Khẩu");
            message.setText("Xin chào " + username + ",\n\n"
                    + "Mã OTP để thiết lập lại mật khẩu tài khoản EduVerse của bạn là: " + otp + "\n"
                    + "Mã số này có hiệu lực trong vòng 5 phút. Sau thời gian này hệ thống sẽ tự động hủy mã.\n\n"
                    + "Trân trọng,\nĐội ngũ vận hành EduVerse.");

            mailSender.send(message);
            log.info("Tiến trình ngầm: Gửi thành công email OTP tới: {}", toEmail);
        } catch (Exception e) {
            log.error("Tiến trình ngầm Brevo: Lỗi quy trình gửi mail tới {}: ", toEmail, e);
        }
    }
}
