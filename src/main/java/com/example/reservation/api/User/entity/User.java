package com.example.reservation.api.User.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "kakao_id", nullable = false)
    private Long kakaoId;

    @CreatedDate
    @Column(name = "create_at", nullable = false)
    private LocalDateTime createAt;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private Role role = Role.USER;

}