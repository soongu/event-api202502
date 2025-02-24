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

        // 이메일이 중복되었지만 회원가입이 아직 마무리되지 않은 회원은 인증코드를 재발송
        if (flag && notFinish(email)) {
            return false;
        }

        // 사용가능한 이메일인 경우 일련의 후속처리 (인증메일 발송, 데이터베이스 처리...)
        if (!flag) {
            processSignup(email);
        }

        return flag;
    }

    private boolean notFinish(String email) {

        // 회원 조회
        EventUser foundUser = eventUserRepository.findByEmail(email).orElseThrow();

        // 이메일인증이 되었는지 여부, 패스워드가 세팅되었는지 여부
        if (!foundUser.isEmailVerified() || foundUser.getPassword() == null) {
            // 인증코드 재생성 및 이메일발송 및 데이터베이스 갱신
            EmailVerification ev = emailVerificationRepository.findByEventUser(foundUser).orElseThrow();
            updateVerificationCode(email, ev);
            return true;
        }
        return false;
    }

    private void processSignup(String email) {
        // 1. 임시 회원가입 (이메일 인증이 안된 상태, 패스워드가 입력되지 않은 상태)
        EventUser tempUser = EventUser.builder()
                .email(email)
                .build();

        EventUser savedUser = eventUserRepository.save(tempUser);

        generateAndSendCode(email, savedUser);

    }

    private void generateAndSendCode(String email, EventUser savedUser) {
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

    /**
     * 사용자가 전송한 인증코드가 발급받은 인증코드와 일치하는지 확인
     * @param email - 사용자의 이메일
     * @param code - 사용자가 전송한 인증코드
     * @return 인증코드가 일치하고 만료되지 않았다면 true, 그게 아니라면 false
     */
    public boolean isMatchCode(String email, String code) {

        // 1. 이메일을 통해 임시회원 가입한 회원정보를 탐색
        EventUser foundUser = eventUserRepository.findByEmail(email).orElseThrow();

        // 2. 인증코드가 있는지 조회
        EmailVerification verificationInfo = emailVerificationRepository.findByEventUser(foundUser).orElseThrow();

        // 3. 코드가 일치하고 만료시간이 지나지 않았는지 체크
        if (
                code.equals(verificationInfo.getVerificationCode())
                && verificationInfo.getExpiryDate().isAfter(LocalDateTime.now())
        ) {
            // 이메일 인증 완료처리
            // EventUser엔터티에서 emailVerified값을 true로 변경
            foundUser.emailVerify();
            eventUserRepository.save(foundUser);

            // 인증코드 삭제
            emailVerificationRepository.delete(verificationInfo);

            return true;
        } else { // 인증코드가 틀렸거나 만료된 경우
            // 인증코드를 재발급해서 이메일을 재발송
            // 새인증코드 발급 및 이메일 전송 데이터베이스 처리 (수정처리)
            updateVerificationCode(email, verificationInfo);
            return false;
        }
    }

    // 인증코드 재발급 처리
    private void updateVerificationCode(String email, EmailVerification emailVerification) {
        // 1. 새인증코드 생성 및 메일발송
        String newCode = sendVerificationEmail(email);

        // 2. 데이터베이스에 수정처리 (새코드, 새 만료시간)
        emailVerification.updateNewCode(newCode);

        // 3. 데이터베이스에 수정 갱신
        emailVerificationRepository.save(emailVerification);
    }
}
