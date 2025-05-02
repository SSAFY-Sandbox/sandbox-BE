package com.ssafy.side.api.fcm.repository;

import com.ssafy.side.api.fcm.domain.FcmToken;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface FcmRepository extends JpaRepository<FcmToken, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM FcmToken f WHERE f.token IN :tokens")
    int deleteByToken(@Param("tokens") List<String> invalidPushTokens);
}
