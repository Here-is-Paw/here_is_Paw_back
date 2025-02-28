package com.ll.hereispaw.domain.payment.payment.repository;

import com.ll.hereispaw.domain.payment.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    @Query("SELECT COUNT(p) > 0 FROM Payment p WHERE p.paymentKey = :paymentKey")
    boolean existsByPaymentKey(@Param("paymentKey") String paymentKey);
}
