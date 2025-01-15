package com.ssafy.side.api.fcm.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.ssafy.side.api.fcm.component.FirebaseApplicationProperties;
import com.ssafy.side.api.fcm.domain.FcmToken;
import com.ssafy.side.api.fcm.dto.FcmTokenRequest;
import com.ssafy.side.api.fcm.dto.FirebaseApplicationPropertiesResponse;
import com.ssafy.side.api.fcm.dto.NotificationRequestDto;
import com.ssafy.side.api.fcm.repository.FcmRepository;
import com.ssafy.side.common.exception.ErrorMessage;
import com.ssafy.side.common.exception.InternalServerException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FcmService {

    private final FcmRepository fcmRepository;
    private final FirebaseApplicationProperties firebaseApplicationProperties;
    private final FirebaseMessaging firebaseMessaging;

    public FirebaseApplicationPropertiesResponse getFirebaseApplicationConfig() {
        return FirebaseApplicationPropertiesResponse.of(firebaseApplicationProperties);
    }

    @Transactional
    public Long saveFcmToken(FcmTokenRequest fcmTokenRequest) {
        FcmToken fcmToken = new FcmToken(fcmTokenRequest.token());
        return fcmRepository.save(fcmToken).getId();
    }

    public void sendNotifications(NotificationRequestDto notificationRequestDto) {
        List<FcmToken> deviceTokens = fcmRepository.findAll();
        deviceTokens.forEach(deviceToken -> {
            try {
                sendFcmMessage(deviceToken.getToken(), notificationRequestDto.title(), notificationRequestDto.body());
            } catch (FirebaseMessagingException e) {
                log.error("Failed to send FCM message", e);
                throw new InternalServerException(ErrorMessage.ERR_FCM_FAILED_TO_SEND);
            }
        });
    }

    private void sendFcmMessage(String deviceToken, String title, String body) throws FirebaseMessagingException {
        Notification notification = Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build();

        Message message = Message.builder()
                .setNotification(notification)
                .setToken(deviceToken)
                .build();

        firebaseMessaging.send(message);
    }
}
