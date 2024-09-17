package com.store.store9m.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.store.store9m.model.OrderResult;

public interface OrderResultRepository extends JpaRepository<OrderResult, Long> {
    // 這裡可以添加自定義查詢方法（可選）
}