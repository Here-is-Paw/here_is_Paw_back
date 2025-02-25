package com.ll.hereispaw.domain.member.mypet.dto.response;

import com.ll.hereispaw.domain.member.mypet.entity.MyPet;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MyPetResponseDto {
    private String name;

    private String breed;

//    @Column(nullable = false)
//    private String imageUrl;

    private String color;
    private String serialNumber;
    private String gender;
    private boolean neutered;
    private int age;
    private String etc;

    public static MyPetResponseDto of(MyPet myPet) {
        MyPetResponseDto dto = new MyPetResponseDto();
        dto.setName(myPet.getName());
        dto.setBreed(myPet.getBreed());
        dto.setColor(myPet.getColor());
        dto.setSerialNumber(myPet.getSerialNumber());
        dto.setGender(convertGender(myPet.getGender()));
        dto.setNeutered(myPet.isNeutered());
        dto.setAge(myPet.getAge());
        dto.setEtc(myPet.getEtc());
        return dto;
    }

    private static String convertGender(int genderCode) {
        return switch (genderCode) {
            case 1 -> "수컷";
            case 2 -> "암컷";
            default -> "-";
        };
    }
}
