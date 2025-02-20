package com.study.event.repository;

import com.study.event.domain.eventUser.entity.EmailVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailVerificationRepository extends JpaRepository<EmailVerification, Long> {
}
