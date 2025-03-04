package com.example.reservation.api.User.controller;


import com.example.reservation.api.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;



}
