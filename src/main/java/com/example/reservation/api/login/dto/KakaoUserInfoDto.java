package com.example.reservation.api.login.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KakaoUserInfoDto {

    private String id;
    private String nickname;
    private String profileImageUrl;
    private String thumbnailImageUrl;
}
