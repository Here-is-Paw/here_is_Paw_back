package com.ll.hereispaw.domain.chat.chatRoom.controller;


import com.ll.hereispaw.domain.chat.chatRoom.dto.ChatRoomDto;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.service.ChatRoomService;
import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.global.globalDto.GlobalResponse;
import com.ll.hereispaw.global.webMvc.LoginUser;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/chat/rooms")
public class ApiV1ChatRoomController {

    private final ChatRoomService chatRoomService;

    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Getter
    public static class requestDto {
        Long targetUserId;
    }

    //채팅방 조회 후 생성 또는 참여
    @PostMapping("")
    public GlobalResponse<ChatRoomDto> create(@LoginUser Member chatUser,@RequestBody requestDto requestDto){
        ChatRoom chatRoom = chatRoomService.createRoomOrView(chatUser, requestDto.targetUserId);
        ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
        return GlobalResponse.success(chatRoomDto);
    }

    /*
    //채팅방 생성
    @PostMapping("/create")
    public GlobalResponse<ChatRoomDto> create(@LoginUser Member chatUser,@RequestParam("targetUserId")Long targetUserId){
        ChatRoom chatRoom = chatRoomService.createRoom(chatUser,targetUserId);
        ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
        return GlobalResponse.success(chatRoomDto);
    }
*/
    /*
    //이제 조회 후 생성 참여로 바꿀거라서 필요없음
    //채팅방 messageId를 이제 마지막 아이디가 누군지 확인해야함
    @GetMapping("/{roomId}")                                                        //required = false 추가하면 필수값이 아니라 선택값으로 받아올 수 있음
    public GlobalResponse<ChatRoomDto> viewRoom(@PathVariable("roomId")Long RoomId, @RequestParam(value="messageId",  required = false) Long messageId){
        ChatRoom chatRoom =this.chatRoomService.viewRoom(RoomId, messageId);
        ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
        return GlobalResponse.success(chatRoomDto);
    }*/
    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //채팅방 목록
    @GetMapping("/list")
    public GlobalResponse<List<ChatRoomDto>>roomList(@LoginUser Member chatUser){
        List<ChatRoom> chatRooms = chatRoomService.roomList(chatUser);

        //chatRoom을 하나하나 chatRoomDto로 변환해서 전송
        List<ChatRoomDto> chatRoomDtos = new ArrayList<>();
        for(ChatRoom chatRoom: chatRooms) {
            ChatRoomDto chatRoomDto = new ChatRoomDto(chatRoom);
            chatRoomDtos.add(chatRoomDto);
        }
        return GlobalResponse.success(chatRoomDtos);
    }


    //채팅방 페이징 버전목록
    /*@GetMapping("")
    public GlobalResponse<Page<ChatRoomDto>> roomList(@RequestParam(value = "page", defaultValue = "0")int page){
        Page<ChatRoom> paging = this.chatRoomService.roomList(page);
        Page<ChatRoomDto> pagingDto = paging.map(chatRoom ->
                new ChatRoomDto(chatRoom)
        );
        return GlobalResponse.success(pagingDto);
    }*/
    /*//채팅방 목록
    @GetMapping("")
    public GlobalResponse<List<ChatRoom>> roomList(){
        List<ChatRoom> list = this.chatRoomService.roomList();
        return GlobalResponse.success(list);
    }*/
    /// /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //채팅방나가기
    @PostMapping("/{roomId}/leave")
    public GlobalResponse<String> leaveRoom(@PathVariable("roomId")Long roomId, @LoginUser Member member){
        chatRoomService.leaveRoom(roomId,member);
        return GlobalResponse.success("채팅방 나가기 성공");
    }
}
