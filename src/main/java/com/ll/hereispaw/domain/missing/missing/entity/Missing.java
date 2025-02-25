package com.ll.hereispaw.domain.missing.missing.entity;

import com.ll.hereispaw.domain.missing.Auhtor.entity.Author;
import com.ll.hereispaw.global.jpa.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.sql.Timestamp;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Missing extends BaseEntity {
    /**
     * 이름, 견종, 유기견 이미지, 지역, 좌표
     * 색상, 동물 등록 번호, 성별, 중성화 유무, 나이, 실종 날짜, 기타(특징), 사례금
     */
    // 필수값
    @NotNull
    @Column(length = 50)
    private String name;
    @NotNull
    @Column(length = 50)
    private String breed;
    @NotNull
    private String geo;
    @NotNull
    private String location;

    @Column(length = 50)
    private String color;

    @Column(length = 15)
    private String serialNumber;
    private boolean gender;
    private boolean neutered;
    private int age;
    private Timestamp lostDate;

    @Column(columnDefinition = "TEXT")
    private String etc;
    private int reward;
    private int state;

//    @NotNull
//    @OneToMany
    private String pathUrl;

    @ManyToOne
    private Author author;
}
