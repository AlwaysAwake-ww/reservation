package com.example.reservation.api.exceptiontest.controller;


import com.example.reservation.api.exceptiontest.dto.BindExceptionTestDto;
import com.example.reservation.api.exceptiontest.dto.TestEnum;
import com.example.reservation.global.error.ErrorCode;
import com.example.reservation.global.error.exception.BusinessException;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/exception")
public class ExceptionTestController {


    @GetMapping("/bind-exception-test")
    public String bindExceptionTest(@Valid BindExceptionTestDto bindExceptionTestDto){
        return "ok";
    }

    @GetMapping("/type-exception-test")
    public String typeMismatchExceptionTest(TestEnum testEnum){

        return "ok";
    }

    @GetMapping("business-exception-test")
    public String businessExceptionTest(String isError){
        if("true".equals(isError))
            throw new BusinessException(ErrorCode.INVALID_REQUEST);
        return "ok";
    }
}
