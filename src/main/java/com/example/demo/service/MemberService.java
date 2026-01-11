package com.example.demo.service;

import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder; // 👈 SecurityConfig에서 생성한 Bean이 주입됨

    public void join(Member member) {
        // 1. 사용자가 입력한 원래 비밀번호를 꺼냅니다.
        String rawPassword = member.getPassword();

        // 2. 비밀번호를 암호화합니다.
        String encodedPassword = passwordEncoder.encode(rawPassword);

        // 3. 암호화된 비밀번호로 다시 설정합니다.
        member.setPassword(encodedPassword);

        // 4. DB에 저장합니다.
        memberRepository.save(member);
    }
}