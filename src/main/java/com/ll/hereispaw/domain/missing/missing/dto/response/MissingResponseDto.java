package com.ll.hereispaw.domain.missing.missing.dto.response;

import com.ll.hereispaw.domain.missing.missing.entity.Missing;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
public class MissingResponseDto {
    /**
     * 이름, 견종, 유기견 이미지, 지역, 좌표
     * 색상, 동물 등록 번호, 성별, 중성화 유무, 나이, 실종 날짜, 기타(특징), 사례금
     */
    private Long id;

    // 필수값
    private String name;
    private String breed;
    private String geo;
    private String location;
    private String pathUrl;
    private String nickname;

    private String color;
    private String serialNumber;
    private int gender;
    private int neutered;
    private int age;
    private LocalDateTime lostDate;
    private String etc;
    private int reward;
    private int missingState;

    public MissingResponseDto(Missing missing) {
        id = missing.getId();
        nickname = missing.getAuthor().getNickname();
        pathUrl = missing.getPathUrl();

        name = missing.getName();
        breed = missing.getBreed();
        geo = missing.getGeo();
        location = missing.getLocation();
        color = missing.getColor();
        serialNumber = missing.getSerialNumber();
        gender = missing.getGender();
        neutered = missing.getNeutered();
        age = missing.getAge();
        lostDate = missing.getLostDate();
        etc = missing.getEtc();
        reward = missing.getReward();
        missingState = missing.getMissingState();
    }
}
