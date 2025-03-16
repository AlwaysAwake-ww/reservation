package com.example.reservation.api.login.service;

import com.example.reservation.api.User.entity.Role;
import com.example.reservation.api.User.entity.User;
import com.example.reservation.api.User.repository.UserRepository;
import com.example.reservation.api.login.entity.KakaoToken;
import com.example.reservation.api.login.repository.KakaoTokenRepository;
import com.example.reservation.global.config.properties.KakaoProperties;
import com.example.reservation.global.redis.listener.RedisListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cglib.core.Local;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final KakaoProperties kakaoProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final KakaoTokenRepository kakaoTokenRepository;
    private final UserRepository userRepository;
    private final RedisListener redisListener;
    /**
     * 카카오 로그인 페이지 URL 생성
     */
    public String getKakaoLoginUri() {
        return "https://kauth.kakao.com/oauth/authorize?client_id="
                + kakaoProperties.getClientId()
                + "&redirect_uri="
                + kakaoProperties.getRedirectUri()
                + "&response_type=code";
    }

    /**
     * 카카오에서 AccessToken 발급받기
     */
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
                String responseBody = response.getBody();
                System.out.println("카카오 응답: " + responseBody); // ✅ DEBUG

                Map<String, Object> body = objectMapper.readValue(responseBody, Map.class);

                // ✅ 파싱된 데이터 검증
                if (!body.containsKey("access_token")) {
                    throw new RuntimeException("카카오 응답에 access_token이 없음: " + responseBody);
                }

                String accessToken = (String) body.get("access_token");
                String refreshToken = (String) body.getOrDefault("refresh_token", null);
                Integer expiresIn = (Integer) body.get("expires_in");
                Integer refreshTokenExpiresIn = (Integer) body.getOrDefault("refresh_token_expires_in", 0);
                String tokenType = (String) body.get("token_type");

                Long kakaoId = getKakaoId(accessToken);
                saveUser(kakaoId);
                saveOrUpdateKakaoToken(kakaoId, accessToken, refreshToken, expiresIn, refreshTokenExpiresIn, tokenType);

                redisListener.saveAccessToken(kakaoId, accessToken, refreshToken, expiresIn, refreshTokenExpiresIn, tokenType);
                redisListener.printRedisData();

                return accessToken;
            } catch (Exception e) {
                throw new RuntimeException("카카오 응답 파싱 실패: " + response.getBody(), e);
            }
        } else {
            throw new RuntimeException("카카오 액세스 토큰 요청 실패. 응답: " + response.getBody());
        }
    }


    /**
     * 사용자 정보 가져오기
     */
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
                throw new RuntimeException("Failed to parse user info response: " + response.getBody(), e);
            }
        } else {
            throw new RuntimeException("Failed to get user info. Response: " + response.getBody());
        }
    }

    /**
     * 액세스 토큰 갱신
     */
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
                System.out.println("카카오 응답 (토큰 갱신): " + response.getBody());

                String newAccessToken = (String) body.get("access_token");
                String newRefreshToken = (String) body.getOrDefault("refresh_token", refreshToken);

                kakaoTokenRepository.save(
                        kakaoToken.toBuilder()
                                .accessToken(newAccessToken)
                                .refreshToken(newRefreshToken)
                                .updatedAt(LocalDateTime.now())
                                .build()
                );

                return newAccessToken;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse refresh token response: " + response.getBody(), e);
            }
        } else {
            throw new RuntimeException("Failed to refresh access token. Response: " + response.getBody());
        }
    }

    /**
     * 카카오 토큰 저장 또는 업데이트
     */
    private void saveOrUpdateKakaoToken(Long kakaoId, String accessToken, String refreshToken,
                                        Integer expiresIn, Integer refreshTokenExpiresIn, String tokenType) {

        Optional<KakaoToken> optionalToken = kakaoTokenRepository.findByKakaoId(kakaoId);

        KakaoToken kakaoToken;
        if (optionalToken.isPresent()) {
            // 기존 토큰 업데이트
            kakaoToken = optionalToken.get();
            kakaoToken = kakaoToken.toBuilder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken != null ? refreshToken : kakaoToken.getRefreshToken()) // null 방지
                    .expiresIn(expiresIn)
                    .refreshTokenExpiresIn(refreshTokenExpiresIn != null ? refreshTokenExpiresIn : kakaoToken.getRefreshTokenExpiresIn()) // null 방지
                    .tokenType(tokenType)
                    .updatedAt(LocalDateTime.now())
                    .build();
        } else {
            kakaoToken = KakaoToken.builder()
                    .kakaoId(kakaoId)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .expiresIn(expiresIn)
                    .refreshTokenExpiresIn(refreshTokenExpiresIn)
                    .tokenType(tokenType)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        kakaoTokenRepository.save(kakaoToken);
    }

    /**
     * 카카오 ID 가져오기
     */
    private Long getKakaoId(String accessToken) {
        Map<String, Object> userInfo = getUserInfo(accessToken);
        return Long.parseLong(String.valueOf(userInfo.get("id")));
    }

    /**
     * 사용자 저장 (새로운 유저이면 DB에 저장)
     */
    private void saveUser(Long kakaoId) {
        User user = userRepository.findByKakaoId(kakaoId).orElseGet(() ->User.builder().kakaoId(kakaoId).createAt(LocalDateTime.now()).role(Role.ROLE_USER).build());
        userRepository.save(user);
    }
}
