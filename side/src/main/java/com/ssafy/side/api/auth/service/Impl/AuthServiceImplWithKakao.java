package com.ssafy.side.api.auth.service.Impl;

import static com.ssafy.side.common.exception.ErrorMessage.ERR_MISSING_AUTHORIZATION_CODE;

import com.ssafy.side.api.auth.dto.SocialInfoDto;
import com.ssafy.side.api.auth.dto.SocialLoginRequestDto;
import com.ssafy.side.api.auth.dto.SocialLoginResponseDto;
import com.ssafy.side.api.auth.service.AuthService;
import com.ssafy.side.api.auth.service.KakaoAuthService;
import com.ssafy.side.api.member.domain.Member;
import com.ssafy.side.api.member.domain.MemberRepository;
import com.ssafy.side.common.config.jwt.JwtTokenProvider;
import com.ssafy.side.common.exception.BadRequestException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImplWithKakao implements AuthService {

    private final KakaoAuthService kakaoAuthService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public SocialLoginResponseDto socialLogin(SocialLoginRequestDto requestDto) {
        if (requestDto.code() == null) {
            throw new BadRequestException(ERR_MISSING_AUTHORIZATION_CODE);
        }

        try {
            SocialInfoDto socialData = kakaoAuthService.getKakaoUserData(requestDto);

            String refreshToken = jwtTokenProvider.generateRefreshToken();

            Optional<Member> findMember = memberRepository.findMemberBySocialId(String.valueOf(socialData.id()));

            // 신규 유저 저장
            if (findMember.isEmpty()) {
                Member member = Member.builder()
                        .nickname(socialData.nickname())
                        .socialId(String.valueOf(socialData.id()))
                        .build();

                memberRepository.save(member);
            }

            // socialId를 통해서 등록된 유저 찾기
            Member signedMember = memberRepository.findMemberBySocialIdOrThrow(String.valueOf(socialData.id()));
            System.out.println(signedMember.getSocialId());
            signedMember.updateRefreshToken(refreshToken);
            String accessToken = jwtTokenProvider.generateAccessToken(signedMember.getId());

            return new SocialLoginResponseDto(accessToken, signedMember.getRefreshToken());

        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    @Transactional
    public void logout(String refreshToken) {
        Member member = memberRepository.findByRefreshTokenOrThrow(refreshToken);
        member.updateRefreshToken(null);
    }

}
