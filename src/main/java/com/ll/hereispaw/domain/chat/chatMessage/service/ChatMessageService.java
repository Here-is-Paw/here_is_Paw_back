package com.ll.hereispaw.domain.chat.chatMessage.service;

import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.chat.chatMessage.repository.ChatMessageRepository;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.service.ChatRoomService;
import com.ll.hereispaw.domain.member.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;

    //메세지 생성
    public ChatMessage writeMessage(Long id, Long messageId, String content, Member member){
        ChatRoom chatRoom = this.chatRoomService.viewRoom(id, messageId);
        if(chatRoom != null){
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setContent(content);
            chatMessage.setMember(member);
            chatMessage.setChatRoom(chatRoom);
            chatMessage.setCreateDate(LocalDateTime.now());
            return this.chatMessageRepository.save(chatMessage);
        }else{
            throw new RuntimeException("error ChatMessageService writeMessage");
        }
    }
}
