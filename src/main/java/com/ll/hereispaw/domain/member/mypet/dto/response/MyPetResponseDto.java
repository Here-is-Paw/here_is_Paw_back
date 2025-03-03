package com.ll.hereispaw.domain.member.mypet.dto.response;

import com.ll.hereispaw.domain.member.mypet.entity.MyPet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MyPetResponseDto {
    private long id;
    private String name;

    private String breed;

//    @Column(nullable = false)
    private String imageUrl;

    private String color;
    private String serialNumber;
    private int gender;
    private boolean neutered;
    private int age;
    private String etc;

    public static MyPetResponseDto of(MyPet myPet) {
        MyPetResponseDto dto = new MyPetResponseDto();
        dto.setId(myPet.getId());
        dto.setName(myPet.getName());
        dto.setBreed(myPet.getBreed());
        dto.setColor(myPet.getColor());
        dto.setSerialNumber(myPet.getSerialNumber());
        dto.setGender(myPet.getGender());
        dto.setNeutered(myPet.isNeutered());
        dto.setAge(myPet.getAge());
        dto.setEtc(myPet.getEtc());
        dto.setImageUrl(myPet.getImageUrl());
        return dto;
    }

}
