package com.ssafy.side.api.email.service;

import com.ssafy.side.api.email.dto.EmailAuthenticationRequestDto;
import com.ssafy.side.api.email.dto.EmailAuthenticationResponseDto;
import com.ssafy.side.api.email.dto.EmailSendResponseDto;
import com.ssafy.side.common.exception.BadRequestException;
import com.ssafy.side.common.exception.ErrorMessage;
import com.ssafy.side.common.exception.InternalServerException;
import com.ssafy.side.common.util.RedisUtil;
import jakarta.mail.internet.MimeMessage;
import java.security.SecureRandom;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;
    private final RedisUtil redisUtil;
    private final SpringTemplateEngine templateEngine;
    @Value("${spring.mail.username}")
    private String emailAddress;
    @Value("${spring.mail.personal}")
    private String replacedEmailAddress;
    @Value("${spring.mail.auth-code-expiration-millis}")
    private long expirationMillis;

    private final String upperCaseLetters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private final String lowerCaseLetters = "abcdefghijklmnopqrstuvwxyz";
    private final String numbers = "0123456789";

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
            messageHelper.setSubject("SANDBOX 이메일 인증");
            messageHelper.setFrom(emailAddress, replacedEmailAddress);
            messageHelper.setTo(email);
            messageHelper.setText(htmlContent, true);
            mailSender.send(message);

            redisUtil.setValueWithExpiration(email, verificationCode, expirationMillis);

            return new EmailSendResponseDto(true);
        } catch (Exception e) {
            log.error("Failed to send verification email to {}: {}", email, e.getMessage(), e);
            throw new InternalServerException(ErrorMessage.ERR_EMAIL_FAILED_TO_SEND);
        }
    }

    public String generateVerificationCode() {
        String allCharacters = upperCaseLetters + lowerCaseLetters + numbers;

        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(6);

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(allCharacters.length());
            code.append(allCharacters.charAt(index));
        }

        return code.toString();
    }

    public EmailAuthenticationResponseDto verifyEmailAuthentication(
            EmailAuthenticationRequestDto emailAuthenticationRequestDto) {
        if (emailAuthenticationRequestDto.email() == null
                || emailAuthenticationRequestDto.authenticationCode() == null) {
            throw new BadRequestException(ErrorMessage.ERR_INVALID_EMAIL_INFO);
        }
        if (!redisUtil.hasKey(emailAuthenticationRequestDto.email()) || !redisUtil.getValue(
                emailAuthenticationRequestDto.email()).equals(emailAuthenticationRequestDto.authenticationCode())) {
            return new EmailAuthenticationResponseDto(false);
        }
        redisUtil.deleteValue(emailAuthenticationRequestDto.email());
        return new EmailAuthenticationResponseDto(true);
    }
}
