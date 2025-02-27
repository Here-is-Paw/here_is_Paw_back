package com.ll.hereispaw.domain.chat.chatRoom.service;

import com.ll.hereispaw.domain.chat.chatMessage.entity.ChatMessage;
import com.ll.hereispaw.domain.chat.chatRoom.entity.ChatRoom;
import com.ll.hereispaw.domain.chat.chatRoom.repository.ChatRoomRepository;
import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    //private final MemberService memberService; 의존을 서비스에서 했는데 findById가 되는지 궁금
    private final MemberRepository memberRepository;

    //채팅방 생성
    public ChatRoom createRoom(Member chatUser,Long targetUserId){
        Member targetUser = memberRepository.findById(targetUserId).orElse(null);
        if (targetUser == null) {
            throw new RuntimeException("상대방 사용자를 찾을 수 없습니다.");
        }

        ChatRoom chatRoom = ChatRoom.builder()
                .chatUser(chatUser)
                .targetUser(targetUser)
                .createDate(LocalDateTime.now())
                //.roomState()
                .build();

        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //채팅방 입장
    public ChatRoom viewRoom(Long id, Long messageId) {
        Optional<ChatRoom> chatRoom = this.chatRoomRepository.findById(id);
        if (chatRoom.isPresent()) {
            ChatRoom cR = chatRoom.get();
            // 자동으로 로딩된 chatMessages 리스트 자동 로딩 되지만 명시적으로 참조해줘야 후속 작업에서 사용할 수 있음
            List<ChatMessage> messages = cR.getChatMessages();
            int index = -1;
            for (int i = 0; i < messages.size(); i++) {
                if (messages.get(i).getId() == messageId) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                messages = messages.subList(index + 1, messages.size());
            }
            return cR;
        } else {
            throw new RuntimeException("error ChatService viewRoom");
        }
    }
    /*
    //채팅방 입장
    public ChatRoom viewRoom(Long id){
        Optional<ChatRoom> chatRoom = this.chatRoomRepository.findById(id);
        if(chatRoom.isPresent()){
            return chatRoom.get();
        }else {
            throw new RuntimeException("error ChatService viewRoom");
        }
    }
*/
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


    // 컨트롤러에서 얘를 먼저 타고 messageId값이 null인지 있는지 확인하고  오버로딩된 메소드로 넘어감
    public ChatRoom createRoomOrView(Member chatUser, Long targetUserId) {
        return createRoomOrView(chatUser, targetUserId, null);
    }

    //사용자간의 채팅방 조회 후 생성 또는 참여
    public ChatRoom createRoomOrView(Member chatUser, Long targetUserId, Long messageId) {
        // 상대방 조회 람다 연습
        Member targetUser = memberRepository.findById(targetUserId)
                .orElseThrow(() -> new RuntimeException("상대방 사용자를 찾을 수 없습니다."));

        Optional<ChatRoom> existingRoom = chatRoomRepository.findByRoom(chatUser, targetUser, targetUser, chatUser);
        
        // 존재하는 채팅방 조회 및 상태 확인
        if (existingRoom.isPresent() && existingRoom.get().getRoomState() != 3) {
            ChatRoom room = existingRoom.get();
            
            // 사용자가 이전에 나갔던 경우 다시 참여시 방 상태 0으로 만듬
            if (room.getChatUser().getId().equals(chatUser.getId()) && room.getRoomState() == 1) {
                room.setRoomState(0);
                chatRoomRepository.save(room);
            } else if (room.getTargetUser().getId().equals(chatUser.getId()) && room.getRoomState() == 2) {
                room.setRoomState(0);
                chatRoomRepository.save(room);
            }
            
            return viewRoom(room.getId(), messageId);
        } else {
            // 채팅방이 없거나 roomState가 3인 경우 새 채팅방 생성
            return createRoom(chatUser, targetUserId);
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //채팅방 목록 조회
    /*public List<ChatRoom> roomList(Member member){
        return chatRoomRepository.findByRoomList(member, member);
    }*/

    //참여된 채팅방 목록 조회
    public List<ChatRoom> roomList(Member member) {
        List<ChatRoom> allRooms = chatRoomRepository.findByRoomList(member, member);
        List<ChatRoom> filteredRooms = new ArrayList<>();

        for (ChatRoom room : allRooms) {
            //지역 변수 재활당 코드 shouldInclude
            boolean shouldInclude = false;

            if (room.getChatUser().getId().equals(member.getId())) {
                // chatUser인 경우 roomState가 0 또는 2인 채팅방만 표시
                if (room.getRoomState() == 0 || room.getRoomState() == 2) {
                    shouldInclude = true;
                }
            } else if (room.getTargetUser().getId().equals(member.getId())) {
                // targetUser인 경우 roomState가 0 또는 1인 채팅방만 표시
                if (room.getRoomState() == 0 || room.getRoomState() == 1) {
                    shouldInclude = true;
                }
            }

            if (shouldInclude) {
                filteredRooms.add(room);
            }
        }

        return filteredRooms;
    }


    /*//채팅방 목록 페이징
    public Page<ChatRoom> roomList(int page){
        //Sort sort = Sort.by(Sort.Order.desc("새로운 채팅 순서 메세지아이디???"));
        Pageable pageable = PageRequest.of(page,10);
    return chatRoomRepository.findAll(pageable);
    }*/
    /*//채팅방 목록
    public List<ChatRoom> roomList(){
        return chatRoomRepository.findAll();
    }*/
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //채팅방 나가기
    public void leaveRoom(Long roomId, Member member){
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));
        //맴버의 객체를 비교하기 때문에 equals 사용
        // chatUser가 나가는 경우
        if(chatRoom.getChatUser().getId().equals(member.getId())){
            if (chatRoom.getRoomState() == 2) {
                // targetUser가 나가서 방 상태가 2이면 3
                chatRoom.setRoomState(3);
            } else {
                //chatUser만 나갔을 경우
                chatRoom.setRoomState(1);
            }
            // targetUser가 나가는 경우
        } else if (chatRoom.getTargetUser().getId().equals(member.getId())) {
            if (chatRoom.getRoomState() == 1) {
                // chatUser가 나가서 방 상태가 1이면 3
                chatRoom.setRoomState(3);
            } else {
                //targetUser만 나간 경우
                chatRoom.setRoomState(2);
            }
        } else {
            throw new RuntimeException("해당 채팅방의 참여자가 아닙니다.");
        }
        chatRoomRepository.save(chatRoom);
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
}
