package com.ll.hereispaw.domain.chat.chatRoom.service;

import com.ll.hereispaw.domain.chat.chatRoom.dto.ChatRoomDto;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    //채팅방 목록
    public Page<ChatRoom> roomList(int page){
        //Sort sort = Sort.by(Sort.Order.desc("새로운 채팅 순서 메세지아이디???"));
        Pageable pageable = PageRequest.of(page,10);
    return chatRoomRepository.findAll(pageable);
    }

    //채팅방
    public ChatRoom viewRoom(Long id){
        Optional<ChatRoom> chatRoom = this.chatRoomRepository.findById(id);
        if(chatRoom.isPresent()){
           ChatRoom CR = chatRoom.get();
           ChatRoomDto ChatRoomDto = new ChatRoomDto(CR);
            return chatRoom.get();
        }else {
            throw new RuntimeException("error ChatService viewRoom");
        }
    }

    //채팅방 생성
    public ChatRoom createRoom(){
        ChatRoom chatRoom = ChatRoom.builder()
                .createDate(LocalDateTime.now())
                //.roomState()
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

}
