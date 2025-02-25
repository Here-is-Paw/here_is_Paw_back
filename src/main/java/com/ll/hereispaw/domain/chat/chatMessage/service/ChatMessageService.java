package com.ll.hereispaw.domain.chat.chatMessage.service;

import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.chat.chatMessage.repository.ChatMessageRepository;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatRoomService chatRoomService;
    private final ChatMessageRepository chatMessageRepository;

    //메세지 생성
    public ChatMessage writeMessage(Long id, String content){

        ChatRoom chatRoom = this.chatRoomService.viewRoom(id);

        if(chatRoom != null){
            ChatMessage chatMessage = new ChatMessage();

            chatMessage.setContent(content);
            chatMessage.setChatRoom(chatRoom);
            chatMessage.setCreateDate(LocalDateTime.now());

            return this.chatMessageRepository.save(chatMessage);
        }else{
            throw new RuntimeException("error ChatMessageService writeMessage");
        }
    }
}
