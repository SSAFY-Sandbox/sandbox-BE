package com.ssafy.side.api.auth.service;

import com.ssafy.side.api.auth.dto.SocialInfoDto;
import com.ssafy.side.api.auth.dto.SocialLoginRequestDto;
import com.ssafy.side.external.kakao.KakaoApiClient;
import com.ssafy.side.external.kakao.KakaoAuthApiClient;
import com.ssafy.side.external.kakao.dto.KakaoAccessTokenInfo;
import com.ssafy.side.external.kakao.dto.KakaoTokenResponse;
import com.ssafy.side.external.kakao.dto.KakaoUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KakaoAuthService {

    @Value(value="${kakao.clientId}")
    private String clientId;

    @Value(value = "${kakao.redirect-uri}")
    private String redirectUri;

    private static final String GRANT_TYPE = "authorization_code";

    private final KakaoAuthApiClient kakaoAuthApiClient;
    private final KakaoApiClient kakaoApiClient;

    public SocialInfoDto getKakaoUserData(SocialLoginRequestDto socialLoginRequestDto) {
        KakaoTokenResponse tokenResponse = kakaoAuthApiClient.getOAuth2Token(
                GRANT_TYPE,
                clientId,
                redirectUri,
                socialLoginRequestDto.code()
        );
        KakaoAccessTokenInfo tokenInfo = kakaoApiClient.getAccessTokenInfo("Bearer " + tokenResponse.getAccessToken());
        KakaoUserResponse userResponse = kakaoApiClient.getUserInformation("Bearer " + tokenResponse.getAccessToken());
        return new SocialInfoDto(tokenInfo.getId(), userResponse.getKakaoAccount().getProfile().getNickname());
    }

}
