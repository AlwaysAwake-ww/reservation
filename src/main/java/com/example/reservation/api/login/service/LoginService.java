package com.example.reservation.api.login.service;

import com.example.reservation.api.login.entity.KakaoToken;
import com.example.reservation.global.config.properties.KakaoProperties;
import com.example.reservation.util.security.AES256Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.example.reservation.api.login.repository.KakaoTokenRepository;


import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final AES256Util aes256Util;
    private final KakaoTokenRepository kakaoTokenRepository;

    public String getKakaoLoginUri() {
        return "https://kauth.kakao.com/oauth/authorize?client_id="
                + kakaoProperties.getClientId()
                + "&redirect_uri="
                + kakaoProperties.getRedirectUri()
                + "&response_type=code";
    }

    public String getAccessToken(String code) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoProperties.getClientId());
        params.add("redirect_uri", kakaoProperties.getRedirectUri());
        params.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(kakaoProperties.getTokenUri(), request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                Map<String, Object> body = objectMapper.readValue(response.getBody(), Map.class);
                String accessToken = (String) body.get("access_token");
                String refreshToken = (String) body.get("refresh_token");
                Integer expiresIn = (Integer) body.get("expires_in");
                Integer refreshTokenExpiresIn = (Integer) body.get("refresh_token_expires_in");
                String tokenType = (String) body.get("token_type");

                // 사용자 Kakao ID 추출
                Long kakaoId = getKakaoId(accessToken);

                // Access Token과 Refresh Token 저장
                saveOrUpdateKakaoToken(kakaoId, accessToken, refreshToken, expiresIn, refreshTokenExpiresIn, tokenType);

                return accessToken;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse access token response.");
            }
        } else {
            throw new RuntimeException("Failed to get access token.");
        }
    }




    public Map<String, Object> getUserInfo(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(kakaoProperties.getUserinfoUri(), HttpMethod.GET, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                return objectMapper.readValue(response.getBody(), Map.class);
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse user info response.");
            }
        } else {
            throw new RuntimeException("Failed to get user info.");
        }
    }
    public String refreshAccessToken(Long kakaoId) {

        KakaoToken kakaoToken = kakaoTokenRepository.findByKakaoId(kakaoId)
                .orElseThrow(() -> new RuntimeException("No existing token found for kakaoId: " + kakaoId));

        String refreshToken = kakaoToken.getRefreshToken();

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "refresh_token");
        params.add("client_id", kakaoProperties.getClientId());
        params.add("refresh_token", refreshToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(kakaoProperties.getTokenUri(), request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                Map<String, Object> body = objectMapper.readValue(response.getBody(), Map.class);

                String newAccessToken = (String) body.get("access_token");
                String newRefreshToken = (String) body.getOrDefault("refresh_token", refreshToken);
                Integer expiresIn = (Integer) body.get("expires_in");
                Integer refreshTokenExpiresIn = (Integer) body.getOrDefault("refresh_token_expires_in", kakaoToken.getRefreshTokenExpiresIn());

                kakaoToken = KakaoToken.builder()
                        .userId(kakaoToken.getUserId())
                        .kakaoId(kakaoToken.getKakaoId())
                        .accessToken(newAccessToken)
                        .refreshToken(newRefreshToken)
                        .expiresIn(expiresIn)
                        .refreshTokenExpiresIn(refreshTokenExpiresIn)
                        .tokenType(kakaoToken.getTokenType())
                        .createdAt(kakaoToken.getCreatedAt())
                        .updatedAt(LocalDateTime.now())
                        .build();

                kakaoTokenRepository.save(kakaoToken);

                return newAccessToken;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse refresh token response.");
            }
        } else {
            throw new RuntimeException("Failed to refresh access token.");
        }
    }

    private Long getKakaoId(String accessToken) {
        Map<String, Object> userInfo = getUserInfo(accessToken);
        return Long.parseLong(String.valueOf(userInfo.get("id")));
    }

    private void saveOrUpdateKakaoToken(Long kakaoId, String accessToken, String refreshToken, Integer expiresIn, Integer refreshTokenExpiresIn, String tokenType) {

        KakaoToken kakaoToken = kakaoTokenRepository.findByKakaoId(kakaoId)
                .map(existingToken -> {
                    existingToken = KakaoToken.builder()
                            .userId(existingToken.getUserId())
                            .kakaoId(existingToken.getKakaoId())
                            .accessToken(accessToken)
                            .refreshToken(refreshToken != null ? refreshToken : existingToken.getRefreshToken())
                            .expiresIn(expiresIn)
                            .refreshTokenExpiresIn(refreshTokenExpiresIn != null ? refreshTokenExpiresIn : existingToken.getRefreshTokenExpiresIn())
                            .tokenType(tokenType)
                            .createdAt(existingToken.getCreatedAt()) // createdAt 유지
                            .updatedAt(LocalDateTime.now())          // updatedAt 갱신
                            .build();
                    return existingToken;
                })
                .orElseGet(() -> {
                    return KakaoToken.builder()
                            .userId(kakaoId)
                            .kakaoId(kakaoId)
                            .accessToken(accessToken)
                            .refreshToken(refreshToken)
                            .expiresIn(expiresIn)
                            .refreshTokenExpiresIn(refreshTokenExpiresIn)
                            .tokenType(tokenType)
                            .createdAt(LocalDateTime.now())  // 신규 생성 시점
                            .updatedAt(LocalDateTime.now())
                            .build();
                });

        kakaoTokenRepository.save(kakaoToken);
    }


}
