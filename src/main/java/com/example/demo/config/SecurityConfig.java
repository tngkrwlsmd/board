package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // 테스트를 위해 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/member/**", "/board/list", "/").permitAll()
                        // 추가: 에러 페이지와 모든 리소스를 일단 다 열어줍니다.
                        .requestMatchers("/error", "/favicon.ico", "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/member/login") // 사용자가 만든 로그인 페이지
                        .loginProcessingUrl("/member/login_proc") // 시큐리티가 낚아챌 주소
                        .usernameParameter("memberId")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/board/list", true) // 성공 시 반드시 목록으로 이동
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/member/logout")
                        .logoutSuccessUrl("/board/list")
                        .permitAll()
                );

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico", "/error");
    }
}