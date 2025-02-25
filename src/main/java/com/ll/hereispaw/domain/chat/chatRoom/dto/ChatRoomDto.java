package com.ll.hereispaw.domain.chat.chatRoom.dto;

import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ChatRoomDto {

    private Long id;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private int roomState;
    private List<ChatMessage> chatMessages;

    public ChatRoomDto(ChatRoom chatRoom){

        this.id = chatRoom.getId();
        this.createdDate = chatRoom.getCreateDate();
        this.modifiedDate = chatRoom.getModifyDate();
        this.roomState = chatRoom.getRoomState();
        this.chatMessages = chatRoom.getChatMessages();
    }
}
