package com.example.reservation.api.login.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name = "KAKAO_TOKEN")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class KakaoToken {

    @Id
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "kakao_id", nullable = false, unique = true)
    private Long kakaoId;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "expires_in", nullable = false)
    private Integer expiresIn;

    @Column(name = "refresh_token_expires_in", nullable = false)
    private Integer refreshTokenExpiresIn;

    @Column(name = "token_type", nullable = false)
    private String tokenType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

}
