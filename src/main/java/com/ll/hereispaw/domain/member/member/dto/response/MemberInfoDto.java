package com.ll.hereispaw.domain.member.member.dto.response;


import com.ll.hereispaw.domain.member.member.entity.Member;
import jakarta.validation.constraints.NotNull;

public record MemberInfoDto(@NotNull String username, @NotNull String nickname, String avatar) {
    public MemberInfoDto(Member member) {
        this(member.getUsername(), member.getNickname(), member.getAvatar());
    }
}