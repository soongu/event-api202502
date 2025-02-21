package com.study.event.service;

import com.study.event.domain.eventUser.entity.EmailVerification;
import com.study.event.domain.eventUser.entity.EventUser;
import com.study.event.repository.EmailVerificationRepository;
import com.study.event.repository.EventUserRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class EventUserService {

    // 메일 발송인의 정보
    @Value("${spring.mail.username}")
    private String mailHost;

    // 이메일 발송을 위한 객체
    private final JavaMailSender mailSender;

    private final EventUserRepository eventUserRepository;
    private final EmailVerificationRepository emailVerificationRepository;

    // 이메일 중복확인 처리
//    @Transactional(readOnly = true)
    public boolean checkEmailDuplicate(String email) {

        boolean flag = eventUserRepository.existsByEmail(email);
        log.info("Checking email {} is duplicate: {}", email, flag);

        // 사용가능한 이메일인 경우 일련의 후속처리 (인증메일 발송, 데이터베이스 처리...)
        if (!flag) {
            processSignup(email);
        }

        return flag;
    }

    private void processSignup(String email) {
        // 1. 임시 회원가입 (이메일 인증이 안된 상태, 패스워드가 입력되지 않은 상태)
        EventUser tempUser = EventUser.builder()
                .email(email)
                .build();

        EventUser savedUser = eventUserRepository.save(tempUser);

        // 2. 인증메일 발송
        String code = sendVerificationEmail(email);

        // 3. 인증코드와 만료시간을 데이터베이스에 저장
        EmailVerification verification = EmailVerification.builder()
                .verificationCode(code)
                .expiryDate(LocalDateTime.now().plusMinutes(5)) // 만료시간 5분
                .eventUser(savedUser) // FK
                .build();

        emailVerificationRepository.save(verification);

    }

    // 이메일 인증코드를 발송하기
    public String sendVerificationEmail(String email) {

        // 4자리 인증 코드 만들기 (1000~9999)
        String code = generateVerificationCode();

        // 메일 전송 로직
        MimeMessage mimeMessage = mailSender.createMimeMessage();

        try {
            MimeMessageHelper messageHelper
                    = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            // 누구에게 이메일을 보낼지
            messageHelper.setTo(email);

            // 누가 보내는 건지
            messageHelper.setFrom(mailHost);

            // 이메일 제목 설정
            messageHelper.setSubject("[인증메일] 중앙정보스터디 가입 인증 메일입니다.");
            // 이메일 내용 설정
            messageHelper.setText(
                    "인증 코드: <b style=\"font-weight: 700; letter-spacing: 5px; font-size: 30px;\">" + code + "</b>"
                    , true
            );

            // 메일 보내기
            mailSender.send(mimeMessage);

            log.info("{} 님에게 이메일이 발송되었습니다.", email);
            return code;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("메일 발송에 실패했습니다.");
        }

    }

    // 무작위 인증코드를 생성하는 기능
    private static String generateVerificationCode() {
        return String.valueOf((int) (Math.random() * 9000 + 1000));
    }
}
