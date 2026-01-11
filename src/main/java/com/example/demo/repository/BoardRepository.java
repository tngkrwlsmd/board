package com.example.demo.repository;

import com.example.demo.entity.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BoardRepository extends JpaRepository<Board, Long> {
    // 제목 검색 (페이징 지원)
    Page<Board> findByTitleContaining(String keyword, Pageable pageable);
    // 내용 검색
    Page<Board> findByContentContaining(String keyword, Pageable pageable);
    // 작성자 검색
    Page<Board> findByWriterContaining(String keyword, Pageable pageable);
    // 전체 목록 (페이징 지원)
    Page<Board> findAll(Pageable pageable);
}