package com.study.event.domain.eventUser.entity;

import com.study.event.domain.event.entity.Event;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder

@Entity
@Table(name = "tbl_event_user")
public class EventUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ev_user_id")
    private Long id;

    @Column(name = "ev_user_email", nullable = false, unique = true)
    private String email;

    // NN을 안거는 이유 : SNS 로그인한 회원, 인증번호만 받고 회원가입을 마무리하지 않은 회원 때문
    @Column(length = 500)
    private String password;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.COMMON; // 권한

    private LocalDateTime createdAt;

    // 이메일 인증을 완료했는지 여부
    @Column(nullable = false)
    private boolean emailVerified;

    // 이벤트와 양방향 연관관계 매핑
    @OneToMany(mappedBy = "eventUser", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Event> eventList = new ArrayList<>();

    // 이메일 인증 완료를 처리하는 메서드
    public void emailVerify() {
        this.emailVerified = true;
    }

    // 회원가입을 마무리하는 메서드
    public void confirm(String password) {
        this.password = password;
        this.createdAt = LocalDateTime.now();
    }

    // 등급업 처리
    public void promotion() {
        this.role = Role.PREMIUM;
    }
}
