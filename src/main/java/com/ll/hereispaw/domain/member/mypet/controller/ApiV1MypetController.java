package com.ll.hereispaw.domain.member.mypet.controller;


import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.mypet.dto.request.MyPetRequest;
import com.ll.hereispaw.domain.member.mypet.dto.response.MyPetResponseDto;
import com.ll.hereispaw.domain.member.mypet.service.MyPetService;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.webMvc.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class ApiV1MypetController {


    private final MyPetService myPetService;

    // 내 반려견 조회
    @GetMapping("/mypet")
    public GlobalResponse<?> getPets(@LoginUser Member loginUser) {
        if (loginUser == null) {
            return GlobalResponse.error(ErrorCode.ACCESS_DENIED);
        }

        List<MyPetResponseDto> myPets = myPetService.getMyPets(loginUser);

        return GlobalResponse.success(myPets);
    }

    // 내 반려견 등록
    @PostMapping("/mypet")
    public GlobalResponse<String> createPet(@LoginUser Member loginUser, @Valid @RequestBody MyPetRequest myPetRequest) {
        if (loginUser == null) {
            return GlobalResponse.error(ErrorCode.ACCESS_DENIED);
        }

        if (myPetRequest == null) {
            return GlobalResponse.error(ErrorCode.INVALID_INPUT_VALUE);
        }

        myPetService.createMyPet(loginUser, myPetRequest);

        return GlobalResponse.createSuccess();
    }

    // 내 반려견 단건 조회
    @GetMapping("/mypet/{id}")
    public GlobalResponse<?> findByMyPet(@LoginUser Member loginUser, @PathVariable Long id) {

        if (loginUser == null) {
            return GlobalResponse.error(ErrorCode.ACCESS_DENIED);
        }

        MyPetResponseDto myPet = myPetService.findByMyPet(loginUser, id);

        return GlobalResponse.success(myPet);
    }

    // 내 반려견 수정
    @PatchMapping("/mypet/{id}")
    public GlobalResponse<String> modifyPet(@LoginUser Member loginUser, @Valid @RequestBody MyPetRequest myPetRequest, @PathVariable Long id) {

        if (loginUser == null) {
            return GlobalResponse.error(ErrorCode.ACCESS_DENIED);
        }

        myPetService.modifyMyPet(loginUser, id, myPetRequest);

        return GlobalResponse.success();
    }

    // 내 반려견 삭제
    @DeleteMapping("/mypet/{id}")
    public GlobalResponse<String> deletePet(@LoginUser Member loginUser, @PathVariable Long id) {

        if (loginUser == null) {
            return GlobalResponse.error(ErrorCode.ACCESS_DENIED);
        }

        myPetService.deleteMyPet(loginUser, id);

        return GlobalResponse.success();
    }
}
