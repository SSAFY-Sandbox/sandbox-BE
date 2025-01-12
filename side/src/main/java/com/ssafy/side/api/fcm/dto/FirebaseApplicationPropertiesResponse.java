package com.ssafy.side.api.fcm.dto;

import com.ssafy.side.api.fcm.component.FirebaseApplicationProperties;

public record FirebaseApplicationPropertiesResponse(
        String apiKey,
        String authDomain,
        String projectId,
        String storageBucket,
        String messagingSenderId,
        String appId,
        String measurementId,
        String vapidKey
) {
    public static FirebaseApplicationPropertiesResponse of(
            FirebaseApplicationProperties firebaseApplicationProperties
    ) {
        return new FirebaseApplicationPropertiesResponse(
                firebaseApplicationProperties.getApiKey(),
                firebaseApplicationProperties.getAuthDomain(),
                firebaseApplicationProperties.getProjectId(),
                firebaseApplicationProperties.getStorageBucket(),
                firebaseApplicationProperties.getMessagingSenderId(),
                firebaseApplicationProperties.getAppId(),
                firebaseApplicationProperties.getMeasurementId(),
                firebaseApplicationProperties.getVapidKey()
        );
    }
}
