package com.ll.hereispaw.domain.member.mypet.service;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.mypet.dto.request.MyPetRequest;
import com.ll.hereispaw.domain.member.mypet.dto.response.MyPetResponseDto;
import com.ll.hereispaw.domain.member.mypet.entity.MyPet;
import com.ll.hereispaw.domain.member.mypet.repository.MyPetRepository;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyPetService {
    @Value("${custom.bucket.name}")
    private String bucketName;

    @Value("${custom.bucket.region}")
    private String region;

    @Value("${custom.bucket.myPet}")
    private String dirName;

    private final S3Client s3Client;
    private final MyPetRepository myPetRepository;

    // 내 반려견 전체 조회
    public List<MyPetResponseDto> getMyPets(Member loginUser) {
        List<MyPet> myPets = myPetRepository.findAllByMember(loginUser);

        return myPets.stream()
                .map(MyPetResponseDto::of)
                .toList();
    }

    // 내 반려견 생성
    @Transactional
    public void createMyPet(Member loginUser, MyPetRequest myPetRequest) {
        try {
            String imageUrl = myPetRequest.hasImageFile()
                    ? uploadImageToS3(myPetRequest.getImageFile())
                    : null;

            log.debug("파일{}", myPetRequest.getImageFile());
            log.debug("파일 이름 : {}", imageUrl);

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
                    .imageUrl(imageUrl)
                    .build();

            myPetRepository.save(myPet);
        } catch (IOException exception) {
            throw new CustomException(ErrorCode.S3_UPLOAD_ERROR);
        }
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
    @Transactional
    public void modifyMyPet(Member loginUser, Long id, MyPetRequest myPetRequest) {
        try {
            // 존재 여부 확인
            MyPet myPet = myPetRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ErrorCode.MY_PET_NOT_FOUND));

            // 권한 확인
            if (!Objects.equals(myPet.getMember().getId(), loginUser.getId())) {
                throw new CustomException(ErrorCode.SC_FORBIDDEN);
            }

            // 파일 존재 확인 및 기존 이미지 S3 삭제 > 새 이미지 등록
            if (myPetRequest.hasImageFile()) {
                deleteImageToS3(myPet.getImageUrl());
                String imageUrl = uploadImageToS3(myPetRequest.getImageFile());
                myPet.setImageUrl(imageUrl);
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
        } catch (IOException exception) {
            throw new CustomException(ErrorCode.S3_UPLOAD_ERROR);
        }
    }

    // 내 반려견 삭제
    @Transactional
    public void deleteMyPet(Member loginUser, Long id) {
        MyPet myPet = myPetRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.MY_PET_NOT_FOUND));

        if (!Objects.equals(myPet.getMember().getId(), loginUser.getId())) {
            throw new CustomException(ErrorCode.SC_FORBIDDEN);
        }

        String imageUrl = myPet.getImageUrl();
        if (imageUrl != null) {
            deleteImageToS3(imageUrl);
        }

        myPetRepository.delete(myPet);
    }

    private String uploadImageToS3(MultipartFile file) throws IOException, NullPointerException {
        if (file == null || file.isEmpty()) {
            return null;
        }

        // 고유한 파일 이름 생성
        String originalFileName = file.getOriginalFilename();
        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
        String filename = UUID.randomUUID() + ext;

        // S3 업로드 요청 생성
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(dirName + "/" + filename)
                .contentType(file.getContentType())
                .build();

        // S3에 파일 업로드
        s3Client.putObject(putObjectRequest,
                RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        // S3 이미지 URL 생성
        return String.format("https://%s.s3.%s.amazonaws.com/%s/%s",
                bucketName, region, dirName, filename);
    }

    private void deleteImageToS3(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(dirName + "/" + fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}