package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // 이 클래스가 데이터베이스 테이블과 매핑됨을 알림
@Getter @Setter // Lombok을 이용해 Getter와 Setter 자동 생성
public class Board {

    @Id // 기본키(PK) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) // MySQL의 auto_increment 사용
    private Long id;

    @Column(nullable = false, length = 100) // null 불가, 길이 제한
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, length = 20)
    private String writer;

    @Column(length = 20)
    private String writerNickname;

    @Column(nullable = false, length = 20)
    private String password;

    @Column(nullable = false)
    private int viewCount = 0;

    private LocalDateTime regDate = LocalDateTime.now(); // 작성 시간 기본값 설정

    @LastModifiedDate // 자동으로 수정시간 기록 (JPA Auditing 설정 시)
    private LocalDateTime modDate;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardFile> files = new ArrayList<>();
}