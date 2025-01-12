package com.ssafy.side.api.fcm.controller;

import com.ssafy.side.api.fcm.dto.FirebaseApplicationPropertiesResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "FCM - FCM 관련 API", description = "FCM API Document")
public interface FcmApi {

    @Operation(summary = "FCM 설정 조회", description = "FCM 설정을 조회하는 API 입니다.")
    ResponseEntity<FirebaseApplicationPropertiesResponse> getFirebaseApplicationConfig();
}
