package com.example.reservation.global.aop;

import com.example.reservation.global.aop.service.ApiHistoryService;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
public class ApiHistoryAop {

    private final ApiHistoryService apiHistoryService;

    @AfterReturning(value = "within(com.example.template.api..*)", returning = "result")
    public void saveLog(JoinPoint joinPoint, Object result) throws JsonProcessingException {

        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        apiHistoryService.saveHistory(joinPoint, result, request);
    }

}
