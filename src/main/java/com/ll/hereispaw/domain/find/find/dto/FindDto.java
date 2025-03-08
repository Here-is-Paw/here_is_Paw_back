package com.ll.hereispaw.domain.find.find.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Point;

@Getter
@Setter
@Builder
public class FindDto {
    // 기존 FindRequest 필드
    private Long id;

    private String title; // 제목
    private String situation; // 발견 상황
    private String breed; // 견종
    private String location; // 지역
    private String name; // 이름
    private String color; // 색상
    private String etc; // 기타 특징

    private Double x;  // Point.getX() 대신 사용
    private Double y;  // Point.getY() 대신 사용

    private int gender; // 성별
    private int age; // 나이
    private int neutered; // 중성화 유무

    private LocalDateTime find_date; // 발견 시간

    private Long member_id; // 신고한 회원 id
    private Long shelter_id; // 보호소 id

    private String path_url;

}
