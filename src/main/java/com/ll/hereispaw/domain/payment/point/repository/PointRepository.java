package com.ll.hereispaw.domain.payment.point.repository;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.payment.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
}
