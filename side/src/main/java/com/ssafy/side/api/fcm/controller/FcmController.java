package com.ssafy.side.api.fcm.controller;

import com.ssafy.side.api.fcm.dto.FirebaseApplicationPropertiesResponse;
import com.ssafy.side.api.fcm.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FcmController implements FcmApi {

    private final FcmService fcmService;

    @Override
    @GetMapping("/fcmconfig")
    public ResponseEntity<FirebaseApplicationPropertiesResponse> getFirebaseApplicationConfig() {
        return ResponseEntity.ok().body(fcmService.getFirebaseApplicationConfig());
    }
}
