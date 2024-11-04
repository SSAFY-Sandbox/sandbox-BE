package com.ssafy.side.api.email.service;

import com.ssafy.side.api.email.dto.EmailSendResponseDto;
import com.ssafy.side.common.exception.EmailSendException;
import com.ssafy.side.common.util.RedisUtil;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.personal}")
    private String replacedEmailAddress;

    @Value("${spring.mail.auth-code-expiration-millis}")
    private long expirationMillis;

    private final String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
    private final String numbers = "0123456789";
    private final String specialCharacters = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    public EmailSendResponseDto sendVerificationEmail(String email) {
        if (redisUtil.hasKey(email)) {
            redisUtil.deleteValue(email);
        }

        Context context = new Context();
        String verificationCode = generateVerificationCode();
        context.setVariable("verificationCode", verificationCode);
        String htmlContent = templateEngine.process("verification-email", context);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setFrom(replacedEmailAddress);
            messageHelper.setTo(email);
            messageHelper.setText(htmlContent, true);
            mailSender.send(message);

            redisUtil.setValueWithExpiration(email, verificationCode, expirationMillis);

            return new EmailSendResponseDto(true);
        } catch (MessagingException e) {
            throw EmailSendException.from(e);
        }
    }

    public String generateVerificationCode() {
        String allCharacters = upperCaseLetters + lowerCaseLetters + numbers + specialCharacters;

        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(allCharacters.length());
            code.append(allCharacters.charAt(index));
        }

        return code.toString();
    }
}
