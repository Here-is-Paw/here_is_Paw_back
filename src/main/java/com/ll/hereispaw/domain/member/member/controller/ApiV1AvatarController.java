package com.ll.hereispaw.domain.member.member.controller;


import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.member.service.MemberService;
import com.ll.hereispaw.global.error.ErrorCode;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.webMvc.LoginUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/profile")
@Slf4j
@Tag(name = " 프로필사진 API", description = "Member")
public class ApiV1AvatarController {

  @Value("${custom.bucket.name}")
  private String bucketName;

  @Value("${custom.bucket.region}")
  private String region;

  @Value("${custom.bucket.avatar}")
  private String dirName;

  private final S3Client s3Client;
  private final MemberService memberService;

  @Operation(summary = "회원 프로필 사진 조회")
  @GetMapping
  public List<String> myprofile(@LoginUser Member loginUser) {
    List<Bucket> bucketList = s3Client.listBuckets().buckets();
    return bucketList.stream().map(Bucket::name).collect(Collectors.toList());
  }

  @Operation(summary = "회원 프로필 사진 업로드")
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public GlobalResponse<String> upload(
      @LoginUser Member loginUser,
      MultipartFile file) {
    try {
      Member member = memberService.findById(loginUser.getId()).get();
      String filename = getUuidFilename(file);
      PutObjectRequest putObjectRequest = PutObjectRequest.builder()
          .bucket(bucketName)
          .key(dirName + "/" + filename)
          .contentType(file.getContentType())
          .build();

      s3Client.putObject(putObjectRequest,
          RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

      member.setAvatar(getS3FileUrl(filename));
      memberService.update(member);


    } catch (IOException e) {
      return GlobalResponse.error(ErrorCode.S3_UPLOAD_ERROR);
    }

    return GlobalResponse.success("업로드 성공");
  }

  @Operation(summary = "회원 프로필 사진 삭제")
  @DeleteMapping
  public GlobalResponse<String> delete(@LoginUser Member loginUser) {
    try {
      Member member = memberService.findById(loginUser.getId()).get();
      String fileName = getFileNameFromS3Url(member.getAvatar());
      DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
          .bucket(bucketName)
          .key(dirName + "/" + fileName)
          .build();
      s3Client.deleteObject(deleteObjectRequest);
      member.setAvatar(getS3FileUrl("defaultAvatar.jpg"));
    } catch (Exception e) {
      log.warn("Failed to delete old profile image", e);
    }
    return GlobalResponse.success("삭제 성공");
  }

  @Operation(summary = "회원 프로필 사진 수정")
  @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public GlobalResponse<String> update(
      @LoginUser Member loginUser,
      MultipartFile file) {
    Member member = memberService.findById(loginUser.getId()).get();

    if (member.getAvatar() != null) {
      delete(loginUser);
      upload(loginUser, file);
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
