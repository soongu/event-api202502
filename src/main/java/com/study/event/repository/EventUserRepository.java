package com.study.event.repository;

import com.study.event.domain.eventUser.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventUserRepository extends JpaRepository<EventUser, Long> {

    // JPA 쿼리메서드로 이메일 중복확인
    boolean existsByEmail(String email);

}
