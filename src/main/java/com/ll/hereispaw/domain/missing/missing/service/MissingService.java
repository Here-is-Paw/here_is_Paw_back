package com.ll.hereispaw.domain.missing.missing.service;

import com.ll.hereispaw.domain.missing.Auhtor.entity.Author;
import com.ll.hereispaw.domain.missing.Auhtor.repository.AuthorRepository;
import com.ll.hereispaw.domain.missing.missing.dto.request.MissingRequestDTO;
import com.ll.hereispaw.domain.missing.missing.dto.response.MissingDTO;
import com.ll.hereispaw.domain.missing.missing.entity.Missing;
import com.ll.hereispaw.domain.missing.missing.repository.MissingRepository;
import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.error.ErrorResponse;
import com.ll.hereispaw.global.exception.CustomException;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.webMvc.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Tag(name = " 실종 신고 API", description = "Missing")
public class MissingService {
    @Value("${custom.bucket.name}")
    private String bucketName;

    @Value("${custom.bucket.region}")
    private String region;

    @Value("${custom.bucket.missing}")
    private String dirName;

    private final S3Client s3Client;

    private final MissingRepository missingRepository;
    private final AuthorRepository authorRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public MissingDTO write(Author author, MissingRequestDTO missingRequestDto,
                            MultipartFile file) {

        log.debug("author : {}", missingRequestDto.getAuthor());

        Missing missing = missingRepository.save(
                Missing.builder()
                        .name(missingRequestDto.getName())
                        .breed(missingRequestDto.getBreed())
                        .geo(missingRequestDto.getGeo())
                        .location(missingRequestDto.getLocation())
                        .color(missingRequestDto.getColor())
                        .serialNumber(missingRequestDto.getSerialNumber())
                        .gender(missingRequestDto.isGender())
                        .neutered(missingRequestDto.isNeutered())
                        .age(missingRequestDto.getAge())
                        .lostDate(missingRequestDto.getLostDate())
                        .etc(missingRequestDto.getEtc())
                        .reward(missingRequestDto.getReward())
                        .state(missingRequestDto.getState())
                        .pathUrl(missingRequestDto.getPathUrl())
                        .author(author)
                        .build()
        );

        s3Upload(missing, file);

        return new MissingDTO(missing);
    }

    public Author of(Member member) {
        return entityManager.getReference(Author.class, member.getId());
    }

    public List<MissingDTO> list() {
        List<Missing> missings = missingRepository.findAll();
        List<MissingDTO> missingDTOS = new ArrayList<>();

        for (Missing missing : missings) {

            Author author = authorRepository.findById(missing.getAuthor().getId()).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

            missingDTOS.add(
                    MissingDTO.builder()
                            .name(missing.getName())
                            .breed(missing.getBreed())
                            .geo(missing.getGeo())
                            .location(missing.getLocation())
                            .color(missing.getColor())
                            .serialNumber(missing.getSerialNumber())
                            .gender(missing.isGender())
                            .neutered(missing.isNeutered())
                            .age(missing.getAge())
                            .lostDate(missing.getLostDate())
                            .etc(missing.getEtc())
                            .reward(missing.getReward())
                            .state(missing.getState())
                            .pathUrl(missing.getPathUrl())
                            .nickname(author.getNickname())
                            .build()
            );
        }

        return missingDTOS;
    }

    public MissingDTO findById(Long missingId) {
        Missing missing = missingRepository.findById(missingId)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSING_NOT_FOUND));

        return new MissingDTO(missing);
    }

    public MissingDTO update(Author author, MissingRequestDTO missingRequestDTO, Long missingId, MultipartFile file) {

        Missing missing = missingRepository.findById(missingId).orElseThrow(() -> new CustomException(ErrorCode.MISSING_NOT_FOUND));

        log.debug("missing : {}", missing);
        log.debug("missing.name : {}", missing.getName());

        // 수정 시 유저 확인
        long loginUserId = missingRequestDTO.getAuthor().getId();

        if(author.getId() == loginUserId) {
            missing.setName(missingRequestDTO.getName());
            missing.setBreed(missingRequestDTO.getBreed());
            missing.setGeo(missingRequestDTO.getGeo());
            missing.setLocation(missingRequestDTO.getLocation());
            missing.setColor(missingRequestDTO.getColor());
            missing.setSerialNumber(missingRequestDTO.getSerialNumber());
            missing.setGender(missingRequestDTO.isGender());
            missing.setNeutered(missingRequestDTO.isNeutered());
            missing.setAge(missingRequestDTO.getAge());
            missing.setLostDate(missingRequestDTO.getLostDate());
            missing.setEtc(missingRequestDTO.getEtc());
            missing.setState(missingRequestDTO.getState());
            missing.setReward(missingRequestDTO.getReward());
            missing.setPathUrl(missingRequestDTO.getPathUrl());

            s3Update(missing, file);

            missingRepository.save(missing);
        }

        return new MissingDTO(missing);
    }

    public String delete(Author author, Long missingId) {
        Missing missing = missingRepository.findById(missingId).orElseThrow(() -> new CustomException(ErrorCode.MISSING_NOT_FOUND));

        Long loginUserId = missing.getAuthor().getId();

        if (Objects.equals(author.getId(), loginUserId)) {
            s3Delete(missing);
            missingRepository.delete(missing);

            return "신고글 삭제";
        }

        return "삭제 권한이 없습니다.";
    }

    // s3 매서드
    public GlobalResponse<String> s3Upload(
            Missing missing,
            MultipartFile file) {
        try {
            String filename = getUuidFilename(file);
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(dirName + "/" + filename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            missing.setPathUrl(getS3FileUrl(filename));

        } catch (IOException e) {
            return GlobalResponse.error(ErrorCode.S3_UPLOAD_ERROR);
        }

        return GlobalResponse.success("업로드 성공");
    }

    public GlobalResponse<String> s3Delete(Missing missing) {
        try {
            String fileName = getFileNameFromS3Url(missing.getPathUrl());
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(dirName + "/" + fileName)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            missing.setPathUrl(getS3FileUrl("defaultAvatar.jpg"));
        } catch (Exception e) {
            log.warn("Failed to delete old profile image", e);
        }
        return GlobalResponse.success("삭제 성공");
    }

    public GlobalResponse<String> s3Update(
            Missing missing,
            MultipartFile file) {

        if (missing.getPathUrl() != null) {
            s3Delete(missing);
            s3Upload(missing, file);
        }

        return GlobalResponse.success("수정 성공");
    }

    private String getUuidFilename(MultipartFile file) {
        // ContentType으로부터 확장자 추출
        String contentType = file.getContentType();
        String extension = switch (contentType) {
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> "jpg";  // 기본값 설정
        };

        // UUID 파일명 생성
        return UUID.randomUUID().toString() + "." + extension;
    }

    public String getS3FileUrl(String fileName) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + dirName + "/" + fileName;
    }

    public String getFileNameFromS3Url(String s3Url) {
        return s3Url.substring(s3Url.lastIndexOf('/') + 1);
    }
}

