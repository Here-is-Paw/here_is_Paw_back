package com.ll.hereispaw.domain.payment.point.service;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.payment.payment.entity.Payment;
import com.ll.hereispaw.domain.payment.point.entity.Point;
import com.ll.hereispaw.domain.payment.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class PointService {
    private final PointRepository pointRepository;

    public void updatePoint(Payment payment) {
        Point userPoint = payment.getUserPoint();
        int pointAmount = payment.getAmount();

        log.info("before point: " + userPoint.getPoints());
        userPoint.addPoints(pointAmount);
        log.info("after point: " + userPoint.getPoints());

        pointRepository.save(userPoint);
    }
}
