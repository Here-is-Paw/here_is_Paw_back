package com.ll.hereispaw.global.event;

import com.ll.hereispaw.domain.payment.payment.entity.Payment;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class PaymentConfirmedEvent extends ApplicationEvent {
    private final Payment payment;

    public PaymentConfirmedEvent(Object source, Payment payment) {
        // source: 이벤트를 발생시킨 객체
        // 부모 클래스인 ApplicationEvent 생성자를 호출하여 source 초기화
        // -> 이벤트가 어떤 객체에서 발생했는지 추적 가능
        super(source);
        this.payment = payment;
    }
}
