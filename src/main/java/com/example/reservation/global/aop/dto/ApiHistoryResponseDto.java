package com.example.reservation.global.aop.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiHistoryResponseDto {


    private String status;
    private String message;
    private String errorCode;

}
