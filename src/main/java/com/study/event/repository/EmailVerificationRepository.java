package com.study.event.repository;

import com.study.event.domain.eventUser.entity.EmailVerification;
import com.study.event.domain.eventUser.entity.EventUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {

    Optional<EmailVerification> findByEventUser(EventUser foundUser);

}
