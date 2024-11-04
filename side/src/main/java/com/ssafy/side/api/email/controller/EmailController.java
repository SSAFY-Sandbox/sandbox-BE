package com.ssafy.side.api.email.controller;

import com.ssafy.side.api.email.dto.EmailSendRequestDto;
import com.ssafy.side.api.email.dto.EmailSendResponseDto;
import com.ssafy.side.api.email.service.EmailService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController implements EmailApi {

    private final EmailService emailService;

    @Override
    @PostMapping
    public ResponseEntity<EmailSendResponseDto> sendEmail(@RequestBody @Valid EmailSendRequestDto emailSendRequestDto) {
        return ResponseEntity.ok().body(emailService.sendVerificationEmail(emailSendRequestDto.email()));
    }
}
