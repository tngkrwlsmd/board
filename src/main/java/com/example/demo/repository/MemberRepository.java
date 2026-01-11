package com.example.demo.repository;

import com.example.demo.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // memberId로 회원을 찾는 메서드 이름을 완성해 보세요!
    Optional<Member> findByMemberId(String memberId);
}