package com.ll.hereispaw.domain.noti.service;

import com.ll.hereispaw.domain.noti.entity.NotiRequest;
import com.ll.hereispaw.domain.noti.entity.Noti;
import com.ll.hereispaw.domain.noti.repository.NotiRepository;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
@RequiredArgsConstructor
@Slf4j
public class SseService {
  private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
  private final NotiRepository notiRepository;

  public SseEmitter add(String userId){
//    if (emitters.containsKey(userId)) {
//      return emitters.get(userId);
//    }
    // 타임아웃 설정 (1시간)
    SseEmitter emitter = new SseEmitter(3600000L);

    // 연결 완료 콜백
    emitter.onCompletion(() -> {
      log.info("SSE connection completed for user: {}", userId);
      this.emitters.remove(userId);
    });

    // 타임아웃 콜백
    emitter.onTimeout(() -> {
      log.info("SSE connection timeout for user: {}", userId);
      emitter.complete();
      this.emitters.remove(userId);
    });

    // 에러 콜백
    emitter.onError((e) -> {
      log.error("SSE connection error for user: {}", userId, e);
      emitter.complete();
      this.emitters.remove(userId);
    });

    // 초기 연결 이벤트 전송
    try {
      emitter.send(SseEmitter.event()
          .name("connect")
          .data("Connected to notification service"));
    } catch (IOException e) {
      log.error("Error sending initial SSE event to user: {}", userId, e);
      emitter.complete();
      return emitter;
    }

    // 사용자 ID로 emitter 저장
    this.emitters.put(userId, emitter);
    log.info("SSE connection established for user: {}", userId);

    return emitter;
  }

  public void sendNoti(Long userId, String eventName, Noti noti){
    String userIdStr = String.valueOf(userId);
    SseEmitter emitter = emitters.get(userIdStr);

    if (emitter != null) {
      try {
        emitter.send(SseEmitter.event()
            .name(eventName)
            .data(noti));

      } catch (IOException e) {
        emitters.remove(userIdStr);
        emitter.completeWithError(e);
      }
    }
  }

//  private void sendUnreadNoti(String userId, SseEmitter emitter) throws IOException {
//    List<Noti> unreadNotifications = notiRepository.findByUserIdAndReadOrderByCreatedAtDesc(userId, false);
//
//    for (Noti noti : unreadNotifications) {
//      emitter.send(SseEmitter.event()
//          .name(noti.getEventName())
//          .data(noti.getNotiRequest())
//          .id(String.valueOf(noti.getId())));
//      log.debug("Unread noti sent: {}", noti.getId());
//      // 전송 후 읽음 처리
//      noti.markAsRead();
//    }
//
//    // 일괄 저장
//    if (!unreadNotifications.isEmpty()) {
//      notiRepository.saveAll(unreadNotifications);
//    }
//  }
//
//  public void sendNotificationToAll(String eventName, NotiRequest data) {
//    emitters.forEach((userId, emitter) -> {
//      sendNoti(userId, eventName, data);
//    });
//  }
//
//  public void removeEmitter(String userId) {
//    emitters.remove(userId);
//  }
}
