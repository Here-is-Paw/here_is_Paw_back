//package com.ll.hereispaw.global.initData;
//
//import com.ll.hereispaw.domain.member.member.entity.Member;
//import com.ll.hereispaw.domain.member.member.service.MemberService;
//import com.ll.hereispaw.domain.missing.Auhtor.entity.Author;
//import com.ll.hereispaw.domain.missing.missing.dto.request.MissingRequestDTO;
//import com.ll.hereispaw.domain.missing.missing.service.MissingService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Profile;
//import org.springframework.core.annotation.Order;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.nio.charset.StandardCharsets;
//import java.sql.Timestamp;
//import java.time.Instant;
//
//@Profile("!prod")
//@Configuration
//@RequiredArgsConstructor
//@Slf4j
//public class InitData {
//    private final MemberService memberService;
//    private final MissingService missingService;
//
//    @Bean
//    @Order(3)
//    public ApplicationRunner initDataNotProd() {
//        return new ApplicationRunner() {
//
//            @Transactional
//            @Override
//            public void run(ApplicationArguments args) {
//                if (memberService.count() > 0) return;
//
//                Member member1 = memberService.signup("user1", "1234", "유저1", "");
//                Member member2 = memberService.signup("user2", "1234", "유저2", "");
//                Member member3 = memberService.signup("user3", "1234", "유저3", "");
//
//                Author author1 = missingService.of(member1);
//                Author author2 = missingService.of(member2);
//                Author author3 = missingService.of(member3);
//
//                // 🟢 실종 동물 데이터 추가
//                String[] names = {"초코", "바둑이", "뽀삐", "구름", "토리", "밤비", "라떼", "뭉치", "달이", "콩이"};
//                String[] breeds = {"푸들", "말티즈", "포메라니안", "비숑", "닥스훈트", "치와와", "코카스파니엘", "슈나우저", "스피츠", "불독"};
//                String[] locations = {"서울 강남구", "부산 해운대구", "대구 중구", "인천 연수구", "광주 동구", "대전 서구", "울산 남구", "수원 장안구", "제주 서귀포시", "청주 상당구"};
//                String[] colors = {"갈색", "흰색", "검정", "회색", "주황색", "베이지", "갈색", "흰색", "검정", "회색"};
//
//                for (int i = 0; i < 10; i++) {
//                    Author assignedAuthor = (i % 3 == 0) ? author1 : (i % 3 == 1) ? author2 : author3;
//
//                    MissingRequestDTO missingRequest = new MissingRequestDTO(
//                            names[i],
//                            breeds[i],
//                            "37.5" + i + ",127.0" + i,
//                            locations[i],
//                            colors[i],
//                            "등록번호" + (i + 1),
//                            (i % 2 == 0),
//                            (i % 2 == 0),
//                            (3 + i),
//                            Timestamp.from(Instant.now().minusSeconds(86400L * i)),
//                            "특징: 활발함",
//                            (i % 2 == 0) ? 100000 : 50000,
//                            1,
//                            assignedAuthor,
//                            "https://example.com/photo" + (i + 1)
//                    );
//
//                    // 🟢 MultipartFile을 사용할 필요가 없거나 null 처리
//                    // 기본 이미지 MultipartFile 변환
//                    MultipartFile file = new MockMultipartFile(
//                            "file",
//                            "example.jpg",
//                            "image/jpeg",
//                            "dummy image content".getBytes(StandardCharsets.UTF_8)
//                    );
//
//                    missingService.write(missingRequest, file);
//
//                    log.info("✅ 실종 등록 완료: {} ({} - {})", missingRequest.getName(), missingRequest.getBreed(), missingRequest.getLocation());
//                }
//            }
//        };
//    }
//}
