package com.ll.hereispaw.domain.payment.point.service;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.payment.payment.entity.Payment;
import com.ll.hereispaw.domain.payment.point.entity.Point;
import com.ll.hereispaw.domain.payment.point.repository.PointRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PointService {
    private final PointRepository pointRepository;

    @Transactional
    public void updatePoint(Payment payment) {
        Point userPoint = payment.getUserPoint();
        int pointAmount = payment.getAmount();

        userPoint.addPoints(pointAmount);

        pointRepository.save(userPoint);
    }
}
