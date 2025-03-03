package com.example.reservation.global.aop.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity(name="API_HISTORY")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "url")
    private String url;

    @Column(name = "ip")
    private String ip;

    @Column(name = "result")
    private String result;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

}
