package com.ll.hereispaw.global.initData;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.missing.Auhtor.entity.Author;
import com.ll.hereispaw.domain.missing.missing.dto.request.MissingRequestDTO;
import com.ll.hereispaw.domain.missing.missing.dto.response.MissingDTO;
import com.ll.hereispaw.domain.missing.missing.service.MissingService;
import com.ll.hereispaw.domain.member.member.service.MemberService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;

@Profile("!prod")
@Configuration
@RequiredArgsConstructor
@Slf4j
public class InitData {
    private final MemberService memberService;
    private final MissingService missingService;

    @Bean
    @Order(3)
    public ApplicationRunner initDataNotProd() {
        return new ApplicationRunner() {

            @Transactional
            @Override
            public void run (ApplicationArguments args) {
                if (memberService.count() > 0)  return;
                Member member1 = memberService.join("user1", "1234", "유저1").getData();
                Member member2 =memberService.join("user1", "1234", "유저1").getData();
                Member member3 =memberService.join("user1", "1234", "유저1").getData();

                Author author1 = missingService.of(member1);
                Author author2 = missingService.of(member2);
                Author author3 = missingService.of(member3);

                // 🟢 실종 동물 데이터 추가
                String[] names = {"초코", "바둑이", "뽀삐", "구름", "토리", "밤비", "라떼", "뭉치", "달이", "콩이"};
                String[] breeds = {"푸들", "말티즈", "포메라니안", "비숑", "닥스훈트", "치와와", "코카스파니엘", "슈나우저", "스피츠", "불독"};
                String[] locations = {"서울 강남구", "부산 해운대구", "대구 중구", "인천 연수구", "광주 동구",
                        "대전 서구", "울산 남구", "수원 장안구", "제주 서귀포시", "청주 상당구"};
                String[] colors = {"갈색", "흰색", "검정", "회색", "주황색", "베이지", "갈색", "흰색", "검정", "회색"};

                for (int i = 0; i < 10; i++) {
                    Author assignedAuthor = (i % 3 == 0) ? author1 : (i % 3 == 1) ? author2 : author3;

                    MissingRequestDTO missingRequest = new MissingRequestDTO(
                            names[i], // 이름
                            breeds[i], // 견종
                            "37.5" + i + ",127.0" + i, // geo 좌표
                            locations[i], // 위치
                            colors[i], // 색상
                            "등록번호" + (i + 1), // 등록 번호
                            (i % 2 == 0), // 성별
                            (i % 2 == 0), // 중성화 여부
                            (3 + i), // 나이
                            Timestamp.from(Instant.now().minusSeconds(86400 * i)), // 실종 날짜
                            "특징: 활발함", // 기타 특징
                            (i % 2 == 0) ? 100000 : 50000, // 사례금
                            1, // 상태
                            assignedAuthor, // 작성자
                            "https://example.com/photo" + (i + 1) // 이미지 URL
                    );

                    missingService.write(missingRequest);
                    log.info("✅ 실종 등록 완료: {} ({} - {})", missingRequest.getName(), missingRequest.getBreed(), missingRequest.getLocation());
                }
            }
        };
    }
}
