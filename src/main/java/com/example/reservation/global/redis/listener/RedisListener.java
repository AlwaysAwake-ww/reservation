package com.example.reservation.global.redis.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class RedisListener {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;


    public RedisListener(RedisTemplate<String, Object> redisTemplate,
                                  ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void saveAccessToken(Long kakaoId, String accessToken, String refreshToken, Integer expiresIn, Integer refreshExpiresIn, String tokenType) throws JsonProcessingException {
        // Redis key: "kakao:token:{kakaoId}"
        String key = "kakao:token:" + kakaoId;

        // 토큰 정보를 Map에 담아서 JSON 문자열로 변환할 수도 있고, Map 그대로 저장할 수도 있습니다.
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("accessToken", accessToken);
        tokenInfo.put("refreshToken", refreshToken);
        tokenInfo.put("expiresIn", expiresIn);
        tokenInfo.put("refreshExpiresIn", refreshExpiresIn);
        tokenInfo.put("tokenType", tokenType);


        String jsonData = objectMapper.writeValueAsString(tokenInfo);


        // Map 자체로 저장 (RedisTemplate이 자동으로 직렬화 처리함)
        redisTemplate.opsForValue().set(key, jsonData);

        // 또는 JSON 문자열로 저장하는 경우
        // String jsonTokenInfo = objectMapper.writeValueAsString(tokenInfo);
        // redisTemplate.opsForValue().set(key, jsonTokenInfo);

        // 토큰 만료 시간에 맞춰 Redis의 TTL(Time To Live) 설정 (예: accessToken 만료 시간)
        redisTemplate.expire(key, Duration.ofSeconds(expiresIn));
    }

    public Map<String, Object> getAccessToken(Long kakaoId) {
        String key = "kakao:token:" + kakaoId;
        Object value = redisTemplate.opsForValue().get(key);
        if (value instanceof Map) {
            return (Map<String, Object>) value;
        }
        return null;
    }

    public void printRedisData(){

        Set<String> keys = redisTemplate.keys("*");


        System.out.println("-------------- redis data ---------------");
        for (String key : keys) {
            String value = (String) redisTemplate.opsForValue().get(key);

            System.out.println("Key: " + key + ", value: " + value);
        }
        System.out.println("-------------- ---------- ---------------");
    }

}
