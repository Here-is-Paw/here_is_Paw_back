package com.ll.hereispaw.domain.payment.point.entity;

import com.ll.hereispaw.domain.payment.payment.entity.Payment;
import com.ll.hereispaw.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Getter
@Setter
@Table(name = "member")
public class Point extends BaseEntity {
    @Column(name="username", unique = true, length = 30)
    private String username;

    // 포인트 기본값 0으로 설정
    @Column(columnDefinition = "INT default 0", nullable = false)
    private Integer points;

    @OneToMany
    private List<Payment> payments;
}
