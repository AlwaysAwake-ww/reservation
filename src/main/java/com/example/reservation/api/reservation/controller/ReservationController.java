package com.example.reservation.api.reservation.controller;


import com.example.reservation.api.reservation.dto.ReservationResponseDto;
import com.example.reservation.api.reservation.service.ReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/reservation")
public class ReservationController {


    private ReservationService reservationService;


    @GetMapping("/getList")
    public ReservationResponseDto getList(){

        return null;
    }

}
