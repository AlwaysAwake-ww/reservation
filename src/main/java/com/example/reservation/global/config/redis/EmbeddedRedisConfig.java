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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;

@RequiredArgsConstructor
@Configuration
public class EmbeddedRedisConfig {

    private RedisServer redisServer;
    private final RedisProperties redisProperties;

    @PostConstruct
    public void initRedis(){

        int port = Integer.parseInt(redisProperties.getPort());
        redisServer = new RedisServer(port);

        if(!isRedisRunning(port)){
            redisServer.start();
        }

    }

    @PreDestroy
    public void stopRedis(){

        redisServer.stop();
    }
/*
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
    }*/

    private boolean isRedisRunning(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            serverSocket.setReuseAddress(true);
            return false;  // 포트가 사용되지 않음 → Redis가 실행되지 않음
        } catch (IOException e) {
            return true;  // 포트가 사용 중임 → Redis가 이미 실행 중임
        }
    }
}


