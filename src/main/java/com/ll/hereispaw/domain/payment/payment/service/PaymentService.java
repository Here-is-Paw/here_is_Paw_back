package com.ll.hereispaw.domain.payment.payment.service;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.payment.point.entity.Point;
import com.ll.hereispaw.domain.payment.payment.entity.Payment;
import com.ll.hereispaw.domain.payment.payment.repository.PaymentRepository;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.event.PaymentConfirmedEvent;
import com.ll.hereispaw.global.exception.CustomException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private final PaymentRepository paymentRepository;
    @PersistenceContext
    private EntityManager entityManager;
    private final ApplicationEventPublisher eventPublisher;

    // 결제 후 포인트를 DB에 저장
    @Transactional
    public synchronized Payment savePaymentData(JSONObject responseData, Point userPoint) {
        // 결제 상태 확인
        // state: "DONE" - 결제 완료
        String status = (String) responseData.get("status");
        if (!"DONE".equals(status)) {
            log.error("Payment failed: status = " + status);
            throw new CustomException(ErrorCode.PAYMENT_FAILED);
        }

        // 결제 금액 추출
        long totalAmount = ((Long) responseData.get("totalAmount"));
        final Integer finalAmount = (int) totalAmount;
        log.info("결제 금액 = " + finalAmount);

        // 중복 결제되었는지 확인
        String paymentKey = (String) responseData.get("paymentKey");
        if (isDuplicatePayment(paymentKey)) {
            log.error("Duplicate payment: paymentKey = " + paymentKey);
            throw new CustomException(ErrorCode.DUPLICATE_PAYMENT);
        }

        // 새로운 결제 내역 생성 및 저장
        try {
            Payment payment = Payment.builder()
                    .userPoint(userPoint)
                    .amount(finalAmount)
                    .paymentKey(paymentKey)
                    .build();

            // 이벤트 발행
            eventPublisher.publishEvent(new PaymentConfirmedEvent(this, payment));
            log.info("Payment saved: paymentKey = {}, amount = {}", paymentKey, finalAmount);

            return paymentRepository.save(payment);
        } catch (Exception e) {
            log.error("Failed to save payment: paymentKey = " + paymentKey);
            throw new CustomException(ErrorCode.PAYMENT_SAVE_FAILED);
        }
    }

    // 중복 결제 체크
    private boolean isDuplicatePayment(String paymentKey) {
        if (paymentKey == null || paymentKey.isBlank()) {
            return false;
        }
        return paymentRepository.existsByPaymentKey(paymentKey);
    }

    public Point of(Member member) {
        return entityManager.getReference(Point.class, member.getId());
    }
}
