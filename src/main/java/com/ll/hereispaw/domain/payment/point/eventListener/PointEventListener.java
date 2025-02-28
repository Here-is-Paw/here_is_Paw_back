package com.ll.hereispaw.domain.payment.point.eventListener;

import com.ll.hereispaw.domain.payment.point.service.PointService;
import com.ll.hereispaw.global.event.PaymentConfirmedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class PointEventListener {
    private final PointService pointService;

    @EventListener
    public void handlePaymentConfirmedEvent(PaymentConfirmedEvent event) {
        pointService.updatePoint(event.getPayment());
    }
}
