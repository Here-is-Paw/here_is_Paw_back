package com.ll.hereispaw.domain.noti.eventListener;

import com.ll.hereispaw.domain.noti.kafka.dto.ImageMatchDto;
import com.ll.hereispaw.domain.noti.kafka.dto.ImageMatchResponseDto;
import com.ll.hereispaw.domain.noti.service.NotiService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
@Slf4j
public class NotiEventListener {

  private final NotiService notiService;

  private static final String FINDING_TO_MISSING = "finding_to_missing";
  private static final String MISSING_TO_FINDING = "missing_to_finding";

  @KafkaListener(
      topics = "dog-face-compare-response",
      groupId = "noti", // 직접 그룹 ID 지정
      containerFactory = "kafkaListenerContainerFactory"
  )
  public void consumeImageMatchResult(ImageMatchResponseDto responseDto) {
    log.info("Received image match result: {}", responseDto);

    if ("success".equals(responseDto.getStatus())) {
      log.info("Found {} matches", responseDto.getMatches().size());

      // 매치 결과 처리 로직
      responseDto.getMatches().forEach(match -> {
        log.info("Match: imageId={}, postId={}, similarity={}",
            match.getImageId(), match.getPostId(), match.getSimilarity());
      });

      List<ImageMatchDto> matches = responseDto.getMatches();

      switch (responseDto.getMessage()){
        case FINDING_TO_MISSING -> {
          notiService.sendToMissingNoti(responseDto.getOriginMemberId(), responseDto.getOriginPostId(), matches);
        }
        case MISSING_TO_FINDING -> {
          notiService.sendToFindingNoti(responseDto.getOriginMemberId(), matches);
        }
      }


    } else {
      log.warn("Image match processing failed with status: {}", responseDto.getStatus());
    }
  }
}
