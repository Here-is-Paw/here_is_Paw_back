package com.ll.hereispaw.domain.payment.point.repository;

import com.ll.hereispaw.domain.payment.point.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    Point findByUsername(String username);
}
