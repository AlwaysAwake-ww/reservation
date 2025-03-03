package com.example.reservation.api.login.controller;


import com.example.reservation.api.login.dto.KakaoLoginResponseDto;
import com.example.reservation.api.login.service.LoginService;
import com.example.reservation.util.jwt.provider.JwtTokenProvider;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/*@Tag(name="Login api", description ="로그인")*/
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/oauth")
public class LoginController {

    private final LoginService loginService;
    private final JwtTokenProvider jwtTokenProvider;

    @Operation(summary = "kakao login page", description = "카카오 로그인페이지")
    @GetMapping("/kakao/login")
    public ResponseEntity<String> kakaoLogin() {
        String loginUrl = loginService.getKakaoLoginUri();
        return ResponseEntity.ok(loginUrl);
    }

    @Operation(summary = "kakao login callback", description = "카카오 로그인 콜백")
    @GetMapping("/kakao/callback")
    public ResponseEntity<KakaoLoginResponseDto> kakaoCallback(@RequestParam String code) {
        String accessToken = loginService.getAccessToken(code);
        var userInfo = loginService.getUserInfo(accessToken);
        String jwtToken = jwtTokenProvider.createToken(userInfo);

        KakaoLoginResponseDto responseDto = KakaoLoginResponseDto.builder()
                .status("success")
                .jwtToken(jwtToken)
                .userInfo(userInfo)
                .build();

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/kakao/getAccessToken")
    public ResponseEntity<String> getKakaoAccessToken (@RequestParam String code){

        String token = loginService.getAccessToken(code);
        return ResponseEntity.ok(token);
    }

    @Operation(summary = "kakao user information", description = "카카오 사용자 정보")
    @GetMapping("/kakao/userinfo")
    public ResponseEntity<?> getUserInfo(@RequestParam String token) {
        var userInfo = loginService.getUserInfo(token);
        return ResponseEntity.ok(userInfo);
    }
}
