package com.ll.hereispaw.domain.payment.point.eventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.hereispaw.domain.payment.point.kafka.dto.PointDto;
import com.ll.hereispaw.domain.payment.point.service.PointService;
import com.ll.hereispaw.global.event.PaymentConfirmedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PointEventListener {
    private final PointService pointService;
    private final ObjectMapper objectMapper;

//    @EventListener
//    public void handlePaymentConfirmedEvent(PaymentConfirmedEvent event) {
//        pointService.updatePoint(event.getPayment());
//    }

    @KafkaListener(
            topics = "payment-confirmed",
            groupId = "point",
            containerFactory = "pointKafkaListenerContainerFactory"
    )
    public void consumePaymentConfirmedEvent(String message) {
        try {
            log.info("Received message: {}", message);
            PointDto pointDto = objectMapper.readValue(message, PointDto.class);
            log.info("Deserialized to PointDto: {}", pointDto);
            pointService.updatePoint(pointDto);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize message: {}", message, e);
        }
    }
}
