package com.ll.hereispaw.domain.member.member.service;

import com.ll.hereispaw.domain.member.member.dto.request.LoginRequest;
import com.ll.hereispaw.domain.member.member.dto.request.SignupRequest;
import com.ll.hereispaw.domain.member.member.dto.response.LoginResponse;
import com.ll.hereispaw.domain.member.member.dto.response.MemberInfoDto;
import com.ll.hereispaw.domain.member.member.entity.Member;
import com.ll.hereispaw.domain.member.member.repository.MemberRepository;
import com.ll.hereispaw.global.rq.Rq;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final AuthTokenService authTokenService;
    private final MemberRepository memberRepository;
    private final Rq rq;
    private final PasswordEncoder passwordEncoder;

    public long count() {
        return memberRepository.count();
    }

    public MemberInfoDto me(Member loginUser) {
        return new MemberInfoDto(loginUser);
    }

    public Member signup(String username, String password, String nickname, String avatar) {
        memberRepository
                .findByUsername(username)
                .ifPresent(user -> {
                    throw new ServiceException("해당 username은 이미 사용중입니다.");
                });

        Member member = Member.builder()
                .username(username)
                .password(password)
                .nickname(nickname)
                .avatar(avatar)
                .apiKey(UUID.randomUUID().toString())
                .build();

        return memberRepository.save(member);
    }

    public Member signup(SignupRequest signupRq) {
        String username = signupRq.username();

        memberRepository
                .findByUsername(username)
                .ifPresent(user -> {
                    throw new ServiceException("해당 username은 이미 사용중입니다.");
                });

        Member member = Member.builder()
                .username(username)
                .password(passwordEncoder.encode(signupRq.password()))
                .nickname(signupRq.nickname())
                .apiKey(UUID.randomUUID().toString())
                .build();

        return memberRepository.save(member);
    }

    public Optional<Member> findByUsername(String username) {
        return memberRepository.findByUsername(username);
    }

    public Optional<Member> findById(long authorId) {
        return memberRepository.findById(authorId);
    }

    public Optional<Member> findByApiKey(String apiKey) {
        return memberRepository.findByApiKey(apiKey);
    }

    public String genAccessToken(Member member) {
        return authTokenService.genAccessToken(member);
    }

    public String genAuthToken(Member member) {
        return member.getApiKey() + " " + genAccessToken(member);
    }

    public Member getMemberFromAccessToken(String accessToken) {
        Map<String, Object> payload = authTokenService.payload(accessToken);

        if (payload == null) return null;

        long id = (long) payload.get("id");
        String username = (String) payload.get("username");
        String nickname = (String) payload.get("nickname");

        Member member = new Member(id, username, nickname);

        return member;
    }

    public void modify(Member member, @NotBlank String nickname, String avatar) {
        member.setNickname(nickname);
        member.setAvatar(avatar);
    }

    public Member modifyOrJoin(String username, String nickname, String avatar) {
        Optional<Member> opMember = findByUsername(username);
        if (opMember.isPresent()) {
            Member member = opMember.get();
            modify(member, nickname, avatar);
            return member;
        }
        return signup(username, "", nickname, avatar);
    }

    public LoginResponse login(LoginRequest loginRq) {
        Member member = memberRepository.findByUsername(loginRq.username())
                .orElseThrow(() -> new EntityNotFoundException("해당 유저는 없습니다."));

        if (passwordEncoder.matches(member.getPassword(), loginRq.password()))
            throw new ServiceException("비밀번호가 일치하지 않습니다.");

        String token = rq.makeAuthCookies(member);

        return new LoginResponse(new MemberInfoDto(member), member.getApiKey(), token);
    }

    public void update(Member member) {
        memberRepository.save(member);
    }
}