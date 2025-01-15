package com.ssafy.side.api.fcm.controller;

import com.ssafy.side.api.fcm.dto.FcmTokenRequestDto;
import com.ssafy.side.api.fcm.dto.FirebaseApplicationPropertiesResponseDto;
import com.ssafy.side.api.fcm.dto.NotificationRequestDto;
import com.ssafy.side.api.fcm.service.FcmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FcmController implements FcmApi {

    private final FcmService fcmService;

    @Override
    @GetMapping("/fcmconfig")
    public ResponseEntity<FirebaseApplicationPropertiesResponseDto> getFirebaseApplicationConfig() {
        return ResponseEntity.ok().body(fcmService.getFirebaseApplicationConfig());
    }

    @Override
    @PostMapping("/fcmtoken")
    public ResponseEntity<Void> saveFcmToken(@Valid @RequestBody FcmTokenRequestDto fcmTokenRequestDto) {
        fcmService.saveFcmToken(fcmTokenRequestDto);
        return ResponseEntity.ok().build();
    }

    @Override
    @PostMapping("/notification")
    public ResponseEntity<Void> sendNotification(@Valid @RequestBody NotificationRequestDto notificationRequestDto) {
        fcmService.sendNotifications(notificationRequestDto);
        return ResponseEntity.ok().build();
    }
}
