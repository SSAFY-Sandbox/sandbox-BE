package com.ssafy.side.api.fcm.service;

import com.ssafy.side.api.fcm.component.FirebaseApplicationProperties;
import com.ssafy.side.api.fcm.domain.FcmToken;
import com.ssafy.side.api.fcm.dto.FcmTokenRequest;
import com.ssafy.side.api.fcm.dto.FirebaseApplicationPropertiesResponse;
import com.ssafy.side.api.fcm.repository.FcmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmRepository fcmRepository;
    private final FirebaseApplicationProperties firebaseApplicationProperties;

    public FirebaseApplicationPropertiesResponse getFirebaseApplicationConfig() {
        return FirebaseApplicationPropertiesResponse.of(firebaseApplicationProperties);
    }

    public Long saveFcmToken(FcmTokenRequest fcmTokenRequest) {
        FcmToken fcmToken = new FcmToken(fcmTokenRequest.token());
        return fcmRepository.save(fcmToken).getId();
    }
}
