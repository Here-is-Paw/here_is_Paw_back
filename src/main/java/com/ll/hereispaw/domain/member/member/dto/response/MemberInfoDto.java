package com.ll.hereispaw.domain.member.member.dto.response;


import com.ll.hereispaw.domain.member.member.entity.Member;
import jakarta.validation.constraints.NotNull;

<<<<<<< HEAD
public record MemberInfoDto(
    @NotNull Long id,
    @NotNull String username,
    @NotNull String nickname,
    String avatar) {
    public MemberInfoDto(Member member) {
        this(member.getId(), member.getUsername(), member.getNickname(), member.getAvatar());
=======
public record MemberInfoDto(@NotNull Long id, @NotNull String nickname, String avatar) {
    public MemberInfoDto(Member member) {
        this(member.getId(), member.getNickname(), member.getAvatar());
>>>>>>> 5462e79dbb7998f5c0fe75e7cc1a9ca2242bc467
    }
}