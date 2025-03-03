package com.ll.hereispaw.domain.payment.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequest {
    // 결제 승인 시 필요한 데이터

    private String orderId;
    private Integer amount;
    private String paymentKey;
}
