package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 댓글들만 가져오는 메서드
    List<Comment> findByBoardId(Long boardId);

    List<Comment> findByBoardIdOrderByRegDateDesc(Long boardId);
}