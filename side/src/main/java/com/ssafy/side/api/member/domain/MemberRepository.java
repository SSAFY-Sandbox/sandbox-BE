package com.ssafy.side.api.member.domain;

import com.ssafy.side.common.exception.ErrorMessage;
import com.ssafy.side.common.exception.NotFoundException;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findMemberById(Long id);

    @Query("SELECT m FROM Member m WHERE m.refreshToken = :refreshToken")
    Optional<Member> findMemberByRefreshToken(@Param("refreshToken") String refreshToken);

    Optional<Member> findMemberBySocialId(String socialId);

    default Member findMemberByIdOrThrow(Long memberId) {
        return findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ERR_NOT_FOUND_MEMBER));
    }

    default Member findByRefreshTokenOrThrow(String refreshToken) {
        return findMemberByRefreshToken(refreshToken)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ERR_NOT_FOUND_MEMBER));
    }

    default Member findMemberBySocialIdOrThrow(String socialId) {
        return findMemberBySocialId(socialId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.ERR_NOT_FOUND_MEMBER));
    }
}

