package com.study.event.domain.eventUser.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_email_verification")
public class EmailVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "verification_id")
    private Long id;

    @Column(nullable = false)
    private String verificationCode; // 인증코드

    @Column(nullable = false)
    private LocalDateTime expiryDate; // 인증 만료시간

    // 연관관계 설정
    @OneToOne
    @JoinColumn(name = "event_user_id", referencedColumnName = "ev_user_id")
    private EventUser eventUser;

    public void updateNewCode(String newCode) {
        this.verificationCode = newCode;
        this.expiryDate = LocalDateTime.now().plusMinutes(5);
    }

    /*
        ALTER TABLE tbl_email_verification
        ADD CONSTRAINT fk_dfsdf_dsfd
        FOREIGN KEY (event_user_id)
        REFERENCES tbl_event_user (ev_user_id)
     */
}
