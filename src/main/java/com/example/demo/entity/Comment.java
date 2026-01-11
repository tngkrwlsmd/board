package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String writer;
    private LocalDateTime regDate = LocalDateTime.now();

    @Column(nullable = false)
    private String password;

    private String writerNickname; // 비회원 닉네임 저장용
    @LastModifiedDate
    private LocalDateTime modDate;

    // 어느 게시글에 달린 댓글인지 연결 (N:1 관계)
    @ManyToOne
    @JoinColumn(name = "board_id")
    private Board board;
}