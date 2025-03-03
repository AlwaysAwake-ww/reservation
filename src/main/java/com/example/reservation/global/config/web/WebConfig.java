package com.example.reservation.global.config.web;

import com.example.reservation.global.interceptor.JwtAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthInterceptor jwtAuthInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("*")
                .allowedMethods(
                        HttpMethod.GET.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.PUT.name(),
                        HttpMethod.PATCH.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name()
                );

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        registry
                .addInterceptor(jwtAuthInterceptor)
                .addPathPatterns("/auth/kakao/userInfo")
                .excludePathPatterns("/auth/kakao/login", "/auth/kakao/callback");

        WebMvcConfigurer.super.addInterceptors(registry);
    }
}
