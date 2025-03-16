package com.example.reservation.util.jwt.provider;


import com.example.reservation.global.config.properties.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.time.Duration;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;

    private Key signingKey;

    @PostConstruct
    protected void init(){

        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
    }

    public String createToken(Map<String, Object> userInfo){

        // 클레임 설정
        // userInfo에서 id 가져와서 subject로 설정
        Claims claims = Jwts.claims().setSubject(userInfo.get("id").toString());

        Map<String, Object> properties = (Map<String, Object>) userInfo.get("properties");
        if(properties != null){
            claims.put("nickname", properties.get("nickname"));
            claims.put("profileImage", properties.get("profile_image"));
        }
/*
        claims.put("nickname", userInfo.get("nickname"));
        claims.put("profileImage", userInfo.get("profile_image"));*/
        claims.put("role", "ROLE_USER");

        ZonedDateTime now = ZonedDateTime.now();
        Instant issuedAt = now.toInstant();
        Instant expiration = now.plus(Duration.ofMillis(Long.parseLong(jwtProperties.getExpirationTime()))).toInstant();


        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(Date.from(issuedAt))
                .setExpiration(Date.from(expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserIdFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }


}
