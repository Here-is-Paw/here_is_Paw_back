package com.ll.hereispaw.domain.noti.controller;

import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.noti.entity.NotiRequest;
import com.ll.hereispaw.domain.noti.entity.Noti;
import com.ll.hereispaw.domain.noti.person.entity.Person;
import com.ll.hereispaw.domain.noti.service.NotiService;
import com.ll.hereispaw.domain.noti.service.SseService;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.webMvc.LoginUser;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/noti")
@RequiredArgsConstructor
@Slf4j
public class NotiController {
  private final NotiService notiService;
  private final SseService sseService;
  private final List<Noti> notiList = new ArrayList<>();

//   알림 목록 조회
  @GetMapping
  public GlobalResponse<List<Noti>> getNotifications(
      @LoginUser Member member) {
    Long memberId = member.getId();
    log.debug("memberId: {}", memberId);
    List<Noti> notifications = notiService.getAllNotifications(memberId);
    log.debug("notifications: {}", notifications.size());
    return GlobalResponse.success(notifications);
  }
//
//  // 알림 읽음 처리
  @GetMapping("/{notiId}/read")
  public ResponseEntity<Void> markAsRead(@PathVariable Long notiId) {
    notiService.markAsRead(notiId);
    return ResponseEntity.ok().build();
  }
//
//  // 모든 알림 읽음 처리
//  @PostMapping("/read-all")
//  public ResponseEntity<Void> markAllAsRead(@AuthenticationPrincipal User user) {
//    Long userId = Long.parseLong(user.getUsername());
//    notiService.markAllAsRead(userId);
//    return ResponseEntity.ok().build();
//  }
}
