package com.example.reservation.global.config.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kakao")
public class KakaoProperties {

    private String clientId;
    private String secret;
    private String redirectUri;
    private String tokenUri;
    private String userinfoUri;
}
