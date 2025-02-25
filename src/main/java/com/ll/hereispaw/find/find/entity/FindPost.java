package com.ll.hereispaw.find.find.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class FindPost {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String breed; // 견종
    private String geo; // 좌표인데 타입 확인 필요
    private String location; // 지역
    private String name; // 이름
    private String color; // 색상
    private String gender; // 성별
    private String etc; // 기타 특징

    private int age; // 나이
    private int state; // 상태

    private boolean neutered; // 중성화 유무

    private LocalDateTime find_date; // 발견 시간

    private Long member_id; // 신고한 회원 id
    private Long shelter_id; // 보호소 id

    public FindPost() {

    }
}
