package com.example.reservation.global.config.redis;

import com.example.reservation.global.config.properties.RedisProperties;
import com.example.reservation.global.error.ErrorCode;
import com.example.reservation.global.error.exception.BusinessException;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import redis.embedded.RedisServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@RequiredArgsConstructor
@Configuration
public class EmbeddedRedisConfig {

    private RedisServer redisServer;
    private final RedisProperties redisProperties;

    @PostConstruct
    public void initRedis(){

        String port = redisProperties.getPort();
        redisServer = new RedisServer();

        redisServer.start();

    }

    @PreDestroy
    public void stopRedis(){

        redisServer.stop();
    }

    private boolean isRunning(Process process) {
        String line;
        StringBuilder pidInfo = new StringBuilder();

        try (BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = input.readLine()) != null) {
                pidInfo.append(line);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return StringUtils.hasText(pidInfo.toString());
    }
}
