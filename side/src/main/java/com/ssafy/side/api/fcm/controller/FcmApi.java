package com.ssafy.side.api.fcm.controller;

import com.ssafy.side.api.fcm.dto.FcmTokenRequest;
import com.ssafy.side.api.fcm.dto.FirebaseApplicationPropertiesResponse;
import com.ssafy.side.api.fcm.dto.NotificationRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "FCM - FCM 관련 API", description = "FCM API Document")
public interface FcmApi {

    @Operation(summary = "FCM 설정 조회", description = "FCM 설정을 조회하는 API 입니다.")
    ResponseEntity<FirebaseApplicationPropertiesResponse> getFirebaseApplicationConfig();

    @Operation(summary = "FCM 토큰 조회", description = "FCM 토큰을 조회하는 API 입니다.")
    ResponseEntity<Void> saveFcmToken(@Valid @RequestBody FcmTokenRequest fcmTokenRequest);

    @Operation(summary = "Notification 테스트", description = "Notification 테스트 API 입니다.")
    ResponseEntity<Void> sendNotification(@Valid @RequestBody NotificationRequestDto notificationRequestDto);
}
