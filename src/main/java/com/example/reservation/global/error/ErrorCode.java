package com.example.reservation.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "4000", "Invalid request parameters"),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "4010", "Unauthorized access"),
    FORBIDDEN(HttpStatus.FORBIDDEN, "4030", "Access forbidden"),

    // 리소스 관련 에러
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "4040", "Requested resource not found"),
    CONFLICT(HttpStatus.CONFLICT, "4090", "Conflict in request"),

    // 내부 서버 에러
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5000", "Internal server error"),

    // 예약 관련 에러
    DUPLICATE_RESERVATION(HttpStatus.CONFLICT, "4091", "Duplicate reservation exists"),
    RESERVATION_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "4001", "Reservation not available"),

    // 결제 관련 에러
    PAYMENT_FAILED(HttpStatus.PAYMENT_REQUIRED, "4020", "Payment processing failed"),

    // 외부 API 에러 (예: 카카오, 결제 게이트웨이 등)
    EXTERNAL_API_ERROR(HttpStatus.SERVICE_UNAVAILABLE, "5030", "Error in external API call");


    ErrorCode(HttpStatus httpStatus, String errorCode, String message){
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
        this.message = message;
    }
    private HttpStatus httpStatus;
    private String errorCode;
    private String message;

}
