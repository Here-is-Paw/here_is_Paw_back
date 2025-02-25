package com.ll.hereispaw.domain.chat.chatRoom.controller;


import com.ll.hereispaw.domain.chat.chatRoom.dto.ChatRoomDto;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.service.ChatRoomService;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/rooms")
public class ApiV1ChatRoomController {

    private final ChatRoomService chatRoomService;

    //채팅방 목록
    @GetMapping("")
    public GlobalResponse<Page<ChatRoomDto>> roomList(@RequestParam(value = "page", defaultValue = "0")int page){
        Page<ChatRoom> paging = this.chatRoomService.roomList(page);
        Page<ChatRoomDto> pagingDto = paging.map(chatRoom ->
                new ChatRoomDto(chatRoom)
        );
        return GlobalResponse.success( pagingDto);
    }

    //채팅방
    @GetMapping("/{roomId}")
    public GlobalResponse<ChatRoomDto> viewRoom(@PathVariable("roomId")Long RoomId){
        ChatRoom chatRoom =this.chatRoomService.viewRoom(RoomId);
        ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
        return GlobalResponse.success(chatRoomDto);
    }

    //채팅방 생성
    @PostMapping("/create")
    public GlobalResponse<ChatRoomDto> create(){
         ChatRoom chatRoom = chatRoomService.createRoom();
         ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
         return GlobalResponse.success(chatRoomDto);
    }
}
