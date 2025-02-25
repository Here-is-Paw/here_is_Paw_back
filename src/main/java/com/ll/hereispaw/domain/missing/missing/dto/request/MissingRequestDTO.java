package com.ll.hereispaw.domain.missing.missing.dto.request;

import com.ll.hereispaw.domain.missing.Auhtor.entity.Author;
import com.ll.hereispaw.domain.missing.missing.entity.Missing;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MissingRequestDTO {
    /**
     * 이름, 견종, 유기견 이미지, 지역, 좌표
     * 색상, 동물 등록 번호, 성별, 중성화 유무, 나이, 실종 날짜, 기타(특징), 사례금
     */
    // 필수값
    @NotBlank(message = "이름은 필수입력입니다.")
    private String name;
    @NotBlank(message = "견종은 필수입력입니다.")
    private String breed;
    @NotBlank(message = "위치는 필수입력입니다.")
    private String geo;
    private String location;

    private String color;
    private String serialNumber;
    private boolean gender;
    private boolean neutered;
    private int age;
    private Timestamp lostDate;
    private String etc;
    private int reward;
    private int state;

    private Author author;
//    @NotBlank(message = "사진은 필수입력입니다.")
    private String pathUrl;

    public MissingRequestDTO(Missing missing) {
        author = missing.getAuthor();
        pathUrl = missing.getPathUrl();

        name = missing.getName();
        breed = missing.getBreed();
        geo = missing.getGeo();
        location = missing.getLocation();
        color = missing.getColor();
        serialNumber = missing.getSerialNumber();
        gender = missing.isGender();
        neutered = missing.isNeutered();
        age = missing.getAge();
        lostDate = missing.getLostDate();
        etc = missing.getEtc();
        reward = missing.getReward();
        state = missing.getState();
    }
}
