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
import org.springframework.transaction.annotation.Transactional;
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
@Transactional(readOnly = true)
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

    @Transactional
    public MissingDTO write(Author author, MissingRequestDTO missingRequestDto) {

//        log.debug("author : {}", missingRequestDto.getAuthor());

        authorRepository.save(author);

        String pathUrl = s3Upload(missingRequestDto.getFile());
        Missing missing = missingRepository.save(
                Missing.builder()
                        .name(missingRequestDto.getName())
                        .breed(missingRequestDto.getBreed())
                        .geo(missingRequestDto.getGeo())
                        .location(missingRequestDto.getLocation())
                        .color(missingRequestDto.getColor())
                        .serialNumber(missingRequestDto.getSerialNumber())
                        .gender(missingRequestDto.getGender())
                        .neutered(missingRequestDto.getNeutered())
                        .age(missingRequestDto.getAge())
//                        .lostDate(missingRequestDto.getLostDate())
                        .etc(missingRequestDto.getEtc())
                        .reward(missingRequestDto.getReward())
                        .missingState(missingRequestDto.getMissingState())
                        .pathUrl(pathUrl)
                        .author(author)
                        .build()
        );

//        s3Upload(missing, file);
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
                    new MissingDTO(missing)
            );
        }

        return missingDTOS;
    }

    public MissingDTO findById(Long missingId) {
        Missing missing = missingRepository.findById(missingId)
                .orElseThrow(() -> new CustomException(ErrorCode.MISSING_NOT_FOUND));

        return new MissingDTO(missing);
    }

    @Transactional
    public MissingDTO update(
            Author author,
            MissingRequestDTO missingRequestDTO,
            Long missingId) {

        Missing missing = missingRepository.findById(missingId).orElseThrow(() -> new CustomException(ErrorCode.MISSING_NOT_FOUND));

        String pathUrl = s3Upload(missingRequestDTO.getFile());

        missing.setName(missingRequestDTO.getName());
        missing.setBreed(missingRequestDTO.getBreed());
        missing.setGeo(missingRequestDTO.getGeo());
        missing.setLocation(missingRequestDTO.getLocation());
        missing.setColor(missingRequestDTO.getColor());
        missing.setSerialNumber(missingRequestDTO.getSerialNumber());
        missing.setGender(missingRequestDTO.getGender());
        missing.setNeutered(missingRequestDTO.getNeutered());
        missing.setAge(missingRequestDTO.getAge());
        missing.setLostDate(missingRequestDTO.getLostDate());
        missing.setEtc(missingRequestDTO.getEtc());
        missing.setMissingState(missingRequestDTO.getMissingState());
        missing.setReward(missingRequestDTO.getReward());
        missing.setPathUrl(pathUrl);

        missingRepository.save(missing);

        return new MissingDTO(missing);
    }

    @Transactional
    public String delete(Author author, Long missingId) {
        Missing missing = missingRepository.findById(missingId).orElseThrow(() -> new CustomException(ErrorCode.MISSING_NOT_FOUND));

        s3Delete(missing.getPathUrl());
        missingRepository.delete(missing);

        return "신고글 삭제";
    }

    // s3 매서드
    public String s3Upload(
            MultipartFile file) {

        String filename = getUuidFilename(file);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(dirName + "/" + filename)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest,
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            //return getS3FileUrl(filename)
//            missing.setPathUrl(getS3FileUrl(filename));

        } catch (IOException e) {
            return new CustomException(ErrorCode.S3_UPLOAD_ERROR).toString();
        }

        return getS3FileUrl(filename);
    }

    public void s3Delete(String fileName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(dirName + "/" + fileName)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
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

