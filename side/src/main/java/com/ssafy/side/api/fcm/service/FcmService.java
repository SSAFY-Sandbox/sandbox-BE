package com.ssafy.side.api.fcm.service;

import com.google.firebase.messaging.BatchResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.MessagingErrorCode;
import com.google.firebase.messaging.MulticastMessage;
import com.google.firebase.messaging.Notification;
import com.google.firebase.messaging.SendResponse;
import com.ssafy.side.api.fcm.component.FirebaseApplicationProperties;
import com.ssafy.side.api.fcm.domain.FcmToken;
import com.ssafy.side.api.fcm.dto.FcmTokenRequestDto;
import com.ssafy.side.api.fcm.dto.FirebaseApplicationPropertiesResponseDto;
import com.ssafy.side.api.fcm.dto.NotificationRequestDto;
import com.ssafy.side.api.fcm.repository.FcmRepository;
import com.ssafy.side.common.exception.ErrorMessage;
import com.ssafy.side.common.exception.InternalServerException;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmService {

    private final FcmRepository fcmRepository;
    private final FirebaseApplicationProperties firebaseApplicationProperties;
    private final FirebaseMessaging firebaseMessaging;

    public FirebaseApplicationPropertiesResponseDto getFirebaseApplicationConfig() {
        return FirebaseApplicationPropertiesResponseDto.of(firebaseApplicationProperties);
    }

    @Transactional
    public Long saveFcmToken(FcmTokenRequestDto fcmTokenRequestDto) {
        FcmToken fcmToken = new FcmToken(fcmTokenRequestDto.token());
        return fcmRepository.save(fcmToken).getId();
    }

    public void sendNotifications(NotificationRequestDto notificationRequestDto) {
        List<FcmToken> deviceTokens = fcmRepository.findAll();

        if (deviceTokens.isEmpty()) {
            return;
        }

        MulticastMessage notificationMessage = buildMulticastMessage(deviceTokens, notificationRequestDto);

        try {
            BatchResponse batchResponse = firebaseMessaging.sendEachForMulticast(notificationMessage);
            handleBatchResponse(batchResponse, deviceTokens);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message", e);
            throw new InternalServerException(ErrorMessage.ERR_FCM_FAILED_TO_SEND);
        }
    }

    private MulticastMessage buildMulticastMessage(List<FcmToken> deviceTokens,
                                                   NotificationRequestDto notificationRequestDto) {
        Notification notification = Notification.builder()
                .setTitle(notificationRequestDto.title())
                .setBody(notificationRequestDto.body())
                .build();

        return MulticastMessage.builder()
                .setNotification(notification)
                .addAllTokens(deviceTokens.stream().map(FcmToken::getToken).toList())
                .build();
    }

    private void handleBatchResponse(BatchResponse response, List<FcmToken> deviceTokens) {
        List<FcmToken> invalidPushTokens = new ArrayList<>();

        List<SendResponse> responses = response.getResponses();

        for (int i = 0; i < responses.size(); i++) {
            if (responses.get(i).isSuccessful()) {
                continue;
            }

            SendResponse failedResponse = responses.get(i);

            FirebaseMessagingException exception = failedResponse.getException();
            log.error("Failed to send message to token {}: {}",
                    deviceTokens.get(i),
                    exception.getMessage());

            MessagingErrorCode errorCode = exception.getMessagingErrorCode();
            if (errorCode == MessagingErrorCode.INVALID_ARGUMENT || errorCode == MessagingErrorCode.UNREGISTERED) {
                invalidPushTokens.add(deviceTokens.get(i));
            }
        }

        log.info("Successfully sent messages: {}/{}",
                response.getSuccessCount(),
                response.getFailureCount() + response.getSuccessCount());

        if (!invalidPushTokens.isEmpty()) {
            int deletedCount = fcmRepository.deleteByToken(
                    invalidPushTokens.stream().map(FcmToken::getToken).toList()
            );
            log.info("Deleted {} invalid push tokens", deletedCount);
        }
    }
}
