package com.ll.hereispaw.domain.chat.chatRoom.repository;

import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.member.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Page<ChatRoom> findAll(Pageable pageable);
    //참여중인 채팅방 목록 조회
    @Query("SELECT c FROM ChatRoom c WHERE c.chatUser = :chatUser OR c.targetUser = :targetUser")
    List<ChatRoom> findByRoomList(@Param("chatUser") Member chatUser, @Param("targetUser") Member targetUser);

    //두 사용자의 채팅방 조회 검증 ?
    @Query("SELECT c FROM ChatRoom c WHERE (c.chatUser = :user1 AND c.targetUser = :user2) OR (c.chatUser = :user2Reverse AND c.targetUser = :user1Reverse)")
    Optional<ChatRoom> findByRoom(
            @Param("user1") Member user1,
            @Param("user2") Member user2,
            @Param("user2Reverse") Member user2Reverse,
            @Param("user1Reverse") Member user1Reverse
    );
}
