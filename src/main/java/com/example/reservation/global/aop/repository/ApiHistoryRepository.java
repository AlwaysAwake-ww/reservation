package com.example.reservation.global.aop.repository;

import com.example.reservation.global.aop.entity.ApiHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiHistoryRepository extends JpaRepository<ApiHistory, String> {


}
