package com.ssafy.side.api.fcm.service;

import com.ssafy.side.api.fcm.component.FirebaseApplicationProperties;
import com.ssafy.side.api.fcm.dto.FirebaseApplicationPropertiesResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {
    private final FirebaseApplicationProperties firebaseApplicationProperties;

    public FirebaseApplicationPropertiesResponse getFirebaseApplicationConfig() {
        return FirebaseApplicationPropertiesResponse.of(firebaseApplicationProperties);
    }
}
