package com.ssafy.side.api.fcm.repository;

import com.ssafy.side.api.fcm.domain.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmRepository extends JpaRepository<FcmToken, Long> {

}
