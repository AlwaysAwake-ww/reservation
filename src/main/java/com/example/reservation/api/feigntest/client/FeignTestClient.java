package com.example.reservation.api.feigntest.client;

import com.example.reservation.api.health.dto.HealthCheckResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;


@FeignClient(url = "http://localhost:8080", name = "feignTestClient")
public interface FeignTestClient {

    @GetMapping(value = "/api/health", consumes = "application/json")
    HealthCheckResponseDto healthCheck();

}
