package com.example.reservation.api.User.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name="email", nullable = false, unique = true)
    private String email;

    @Column(name="name", nullable = false)
    private String name;

    @Column(name="phone", nullable = false)
    private String phone;

    @Column(name="kakao_id", nullable = false)
    private Long kakaoId;

    @Enumerated(EnumType.STRING)
    @Column(name="role", nullable = false)
    private Role role = Role.USER;

}