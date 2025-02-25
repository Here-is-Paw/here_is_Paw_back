package com.ll.hereispaw.domain.member.mypet.service;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.mypet.dto.request.MyPetRequest;
import com.ll.hereispaw.domain.member.mypet.dto.response.MyPetResponseDto;
import com.ll.hereispaw.domain.member.mypet.entity.MyPet;
import com.ll.hereispaw.domain.member.mypet.repository.MyPetRepository;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class MyPetService {

    private final MyPetRepository myPetRepository;

    // 내 반려견 전체 조회
    public List<MyPetResponseDto> getMyPets(Member loginUser) {
        List<MyPet> myPets = myPetRepository.findAllByMember(loginUser);

        return myPets.stream()
                .map(MyPetResponseDto::of)
                .toList();
    }

    // 내 반려견 생성
    public void createMyPet(Member loginUser, MyPetRequest myPetRequest) {

        MyPet myPet = MyPet.builder()
                .member(loginUser)
                .name(myPetRequest.getName())
                .breed(myPetRequest.getBreed())
                .color(myPetRequest.getColor())
                .serialNumber(myPetRequest.getSerialNumber())
                .gender(myPetRequest.getGender() != null ? myPetRequest.getGender() : 0)
                .neutered(myPetRequest.isNeutered())
                .age(myPetRequest.getAge() != null ? myPetRequest.getAge() : 0)
                .etc(myPetRequest.getEtc())
                .build();

        myPetRepository.save(myPet);
    }

    // 내 반려견 단건 조회
    public MyPetResponseDto findByMyPet(Member loginUser, Long id) {
        MyPet myPet = myPetRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MY_PET_NOT_FOUND));

        if (!Objects.equals(myPet.getMember().getId(), loginUser.getId())) {
            throw new CustomException(ErrorCode.SC_FORBIDDEN);
        }

        return MyPetResponseDto.of(myPet);
    }

    // 내 반려견 수정
    public void modifyMyPet(Member loginUser, Long id, MyPetRequest myPetRequest) {
        MyPet myPet = myPetRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MY_PET_NOT_FOUND));

        if (!Objects.equals(myPet.getMember().getId(), loginUser.getId())) {
            throw new CustomException(ErrorCode.SC_FORBIDDEN);
        }

        myPet.setName(myPetRequest.getName());
        myPet.setBreed(myPetRequest.getBreed());

        if (myPetRequest.hasColor()) {
            myPet.setColor(myPetRequest.getColor());
        }

        if (myPetRequest.hasSerialNumber()) {
            myPet.setSerialNumber(myPetRequest.getSerialNumber());
        }

        if (myPetRequest.hasGender()) {
            myPet.setGender(myPetRequest.getGender());
        }

        myPet.setNeutered(myPetRequest.isNeutered());

        if (myPetRequest.hasAge()) {
            myPet.setAge(myPetRequest.getAge());
        }

        if (myPetRequest.hasEtc()) {
            myPet.setEtc(myPetRequest.getEtc());
        }

        myPetRepository.save(myPet);
    }

    // 내 반려견 삭제
    public void deleteMyPet(Member loginUser, Long id) {
        MyPet myPet = myPetRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MY_PET_NOT_FOUND));

        if (!Objects.equals(myPet.getMember().getId(), loginUser.getId())) {
            throw new CustomException(ErrorCode.SC_FORBIDDEN);
        }

        myPetRepository.delete(myPet);
    }
}