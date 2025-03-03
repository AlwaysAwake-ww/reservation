package com.example.reservation.api.feigntest.controller;

import com.example.reservation.api.feigntest.client.FeignTestClient;
import com.example.reservation.api.feigntest.client.GitHubClient;
import com.example.reservation.api.feigntest.client.GitHubUser;
import com.example.reservation.api.health.dto.HealthCheckResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class FeignTestController {

    private final FeignTestClient feignTestClient;
    private final GitHubClient gitHubClient;

    @GetMapping("/health/feign-test")
    public ResponseEntity<HealthCheckResponseDto> healthCheckTest() {
        HealthCheckResponseDto healthCheckResponseDto = feignTestClient.healthCheck();
        return ResponseEntity.ok(healthCheckResponseDto);
    }

    @GetMapping("/user/{username}")
    public GitHubUser getUser(@PathVariable String username) {
        return gitHubClient.getUser(username);
    }
}
