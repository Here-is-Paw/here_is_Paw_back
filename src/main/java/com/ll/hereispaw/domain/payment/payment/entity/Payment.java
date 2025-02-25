package com.ll.hereispaw.domain.payment.payment.entity;

import com.ll.hereispaw.domain.member.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "payments")
@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Payment {
    @Id
    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;          // 결제한 회원

    @Column(nullable = false)
    private Integer amount;          // 실제 결제 금액
}
