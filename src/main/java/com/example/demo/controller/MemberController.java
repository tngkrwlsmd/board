package com.example.demo.controller;

import com.example.demo.entity.Member;
import com.example.demo.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 1. 가입 페이지 보여주기
    @GetMapping("/member/join")
    public String joinForm() {
        return "join";
    }

    // 2. 가입 처리하기
    @PostMapping("/member/join")
    public String join(Member member) {
        memberService.join(member);
        return "redirect:/member/login?success"; // 뒤에 ?success를 붙여서 가입 성공을 알릴 수도 있어요!
    }

    @GetMapping("/member/login")
    public String loginForm() {
        return "login"; // login.jsp를 보여줍니다.
    }
}