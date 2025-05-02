package com.ssafy.side.api.fcm.component;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "firebase")
@PropertySource("classpath:firebase-api-key.properties")
public class FirebaseApplicationProperties {

    @NotEmpty(message = "API Key가 비어있습니다.")
    private String apiKey;

    @NotEmpty(message = "Auth Domain가 비어있습니다.")
    private String authDomain;

    @NotEmpty(message = "Project Id가 비어있습니다.")
    private String projectId;

    @NotEmpty(message = "Storage Bucket가 비어있습니다.")
    private String storageBucket;

    @NotEmpty(message = "Messaging Sender Id가 비어있습니다.")
    private String messagingSenderId;

    @NotEmpty(message = "App Id가 비어있습니다.")
    private String appId;

    @NotEmpty(message = "Measurement Id가 비어있습니다.")
    private String measurementId;

    @NotEmpty(message = "Vapid Key가 비어있습니다.")
    private String vapidKey;
}
