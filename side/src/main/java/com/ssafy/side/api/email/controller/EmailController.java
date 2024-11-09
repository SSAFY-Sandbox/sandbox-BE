package com.ssafy.side.api.email.controller;

import com.ssafy.side.api.email.dto.EmailAuthenticationRequestDto;
import com.ssafy.side.api.email.dto.EmailAuthenticationResponseDto;
import com.ssafy.side.api.email.dto.EmailSendRequestDto;
import com.ssafy.side.api.email.dto.EmailSendResponseDto;
import com.ssafy.side.api.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController implements EmailApi {

    private final EmailService emailService;

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmailSendResponseDto> sendEmail(
            @RequestBody @Valid EmailSendRequestDto emailSendRequestDto
    ) {
        return ResponseEntity.ok().body(emailService.sendVerificationEmail(emailSendRequestDto.email()));
    }

    @Override
    @PostMapping("/authentication")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<EmailAuthenticationResponseDto> verifyEmailAuthentication(
            @RequestBody @Valid EmailAuthenticationRequestDto emailAuthenticationRequestDto
    ) {
        return ResponseEntity.ok().body(emailService.verifyEmailAuthentication(emailAuthenticationRequestDto));
    }
}
