package com.ll.hereispaw.domain.payment.payment.service;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.member.service.MemberService;
import com.ll.hereispaw.domain.payment.payment.entity.Payment;
import com.ll.hereispaw.domain.payment.payment.repository.PaymentRepository;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final MemberService memberService;

    // 결제 후 포인트를 DB에 저장
    @Transactional
    public synchronized Payment savePaymentData(JSONObject responseData, Member member) {
        // 결제 상태 확인
        String status = (String) responseData.get("status");
        if (!"DONE".equals(status)) {
            throw new CustomException(ErrorCode.PAYMENT_FAILED);
        }

        // 결제 금액 추출
        long totalAmount = ((Long) responseData.get("totalAmount"));
        final Integer finalAmount = (int) totalAmount;

        // 중복 결제되었는지 확인
        String paymentKey = (String) responseData.get("paymentKey");
        if (isDuplicatePayment(paymentKey)) {
            throw new CustomException(ErrorCode.DUPLICATE_PAYMENT);
        }

        // 새로운 결제 내역 생성 및 저장
        try {
            Payment payment = Payment.builder()
                    .member(member)
                    .amount(finalAmount)
                    .paymentKey(paymentKey)
                    .build();

            // 회원 포인트 업데이트
            memberService.updateMemberPoints(member, finalAmount);

            return paymentRepository.save(payment);
        } catch (Exception e) {
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

    // 회원의 총 포인트 조회
    public Integer getPointsByMemberId(Long memberId) {
        return paymentRepository.getTotalPointsByMemberId(memberId);
    }
}
