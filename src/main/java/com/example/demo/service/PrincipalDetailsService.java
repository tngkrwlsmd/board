package com.example.demo.service;

import com.example.demo.entity.Member;
import com.example.demo.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 시큐리티는 'username'이라는 이름으로 아이디를 찾습니다.
        // 우리는 memberId를 사용하므로 여기서 찾아줍니다.
        Member memberEntity = memberRepository.findByMemberId(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 사용자를 찾을 수 없습니다: " + username));

        // 시큐리티 전용 'User' 객체로 변환해서 반환합니다.
        return User.builder()
                .username(memberEntity.getMemberId())
                .password(memberEntity.getPassword())
                .roles("USER") // 권한 설정 (기본 USER)
                .build();
    }
}