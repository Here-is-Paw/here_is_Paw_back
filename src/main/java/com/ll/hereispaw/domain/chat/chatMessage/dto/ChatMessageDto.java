package com.ll.hereispaw.domain.chat.chatMessage.dto;

import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageDto {

    private Long chatMessageId;
    private Long chatRoomId;
    private String content;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;

    public ChatMessageDto(ChatMessage chatMessage){

        this.chatMessageId      = chatMessage.getId();
        this.chatRoomId         = chatMessage.getChatRoom().getId();
        this.content            = chatMessage.getContent();
        this.createdDate        = chatMessage.getCreateDate();
        this.modifiedDate       = chatMessage.getModifyDate();

    }
}
