package com.ll.hereispaw.domain.find.find.service;

import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;
import java.util.UUID;

@Service
public class FindImageService {
    public String saveBase64Image(String base64Image) {
        try {
            // Base64 데이터에서 헤더 제거
            String base64Data = base64Image.contains(",") ? base64Image.split(",")[1] : base64Image;

            // 확장자 판별
            String extension = "png"; // 기본 확장자
            if (base64Image.startsWith("data:image/jpeg")) {
                extension = "jpg";
            } else if (base64Image.startsWith("data:image/gif")) {
                extension = "gif";
            }

            // 디코딩
            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            // 저장할 파일 이름 생성
            String fileName = UUID.randomUUID().toString() + "." + extension;
            String uploadDir = "uploaded_images/";

            // 디렉토리 생성
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 파일 저장
            File file = new File(uploadDir + fileName);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(imageBytes);
            }

            // 반환할 URL (서버가 정적 파일을 제공한다고 가정)
            return "http://localhost:8090/uploaded_images/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("이미지 저장 실패: " + e.getMessage());
        }
    }

}
