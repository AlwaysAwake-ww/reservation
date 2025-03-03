package com.example.reservation.api.login.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class KakaoLoginResponseDto {

    private String status;
    private String jwtToken;
    private Map<String, Object> userInfo;
}
