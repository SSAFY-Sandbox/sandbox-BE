package com.ssafy.side.api.email.controller;

import com.ssafy.side.api.email.dto.EmailAuthenticationRequestDto;
import com.ssafy.side.api.email.dto.EmailAuthenticationResponseDto;
import com.ssafy.side.api.email.dto.EmailSendRequestDto;
import com.ssafy.side.api.email.dto.EmailSendResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "Email - 이메일 관련 API", description = "Email API Document")
public interface EmailApi {

    @Operation(summary = "이메일 전송", description = "이메일을 전송하는 API 입니다.")
    ResponseEntity<EmailSendResponseDto> sendEmail(@RequestBody @Valid EmailSendRequestDto emailSendRequestDto);

    @Operation(summary = "이메일 인증 번호 검증", description = "이메일 인증 번호를 검증하는 API 입니다.")
    ResponseEntity<EmailAuthenticationResponseDto> verifyEmailAuthentication(
            @RequestBody @Valid EmailAuthenticationRequestDto emailAuthenticationRequestDto);
}
