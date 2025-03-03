package com.ll.hereispaw.domain.member.member.dto.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

public record ModifyRequest(@NotNull String username, MultipartFile profile, String nickname){

    public boolean hasProfile() {
        return profile != null;
    }

    public boolean hasNickname() {
        return nickname != null;
    }

}

