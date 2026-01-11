package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.entity.Comment;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;

    // 프로젝트 내 파일 저장 경로 (멤버 변수로 관리)
    private final String uploadPath = System.getProperty("user.dir") + "/src/main/resources/static/files";

    @GetMapping("/")
    public String index() {
        return "redirect:/board/list";
    }

    // --- [게시글 관련] ---

    @GetMapping("/board/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "1") int page,
                       @RequestParam(value = "searchType", required = false) String searchType,
                       @RequestParam(value = "keyword", required = false) String keyword) {

        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by("id").descending());
        Page<Board> boardPage;

        if (keyword != null && !keyword.trim().isEmpty()) {
            if ("title".equals(searchType)) boardPage = boardRepository.findByTitleContaining(keyword, pageable);
            else if ("content".equals(searchType)) boardPage = boardRepository.findByContentContaining(keyword, pageable);
            else if ("writer".equals(searchType)) boardPage = boardRepository.findByWriterContaining(keyword, pageable);
            else boardPage = boardRepository.findAll(pageable);
        } else {
            boardPage = boardRepository.findAll(pageable);
        }

        model.addAttribute("list", boardPage.getContent());
        model.addAttribute("page", boardPage);
        return "list";
    }

    @GetMapping("/board/write")
    public String writeForm() {
        return "write";
    }

    @PostMapping("/board/write")
    public String write(Board board, @RequestParam("file") MultipartFile file,
                        HttpServletRequest request) throws IOException { // request 추가

        // 비회원 글인 경우 (비밀번호가 고정값이 아님) IP 저장
        if (!"SECURED_MEMBER_POST".equals(board.getPassword())) {
            String ip = request.getRemoteAddr();
            board.setWriter(ip);
        }

        // 파일 저장 로직
        if (!file.isEmpty()) {
            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files";

            // 폴더가 존재하는지 확인하고, 없으면 생성
            File dir = new File(projectPath);
            if (!dir.exists()) {
                dir.mkdirs(); // 하위 폴더까지 모두 생성
            }

            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();

            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);

            board.setFileName(fileName);
            board.setFileOriginName(file.getOriginalFilename());
        }

        boardRepository.save(board);
        return "redirect:/board/list";
    }

    @GetMapping("/board/view/{id}")
    public String view(Model model, @PathVariable("id") Long id) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null) {
            board.setViewCount(board.getViewCount() + 1);
            boardRepository.save(board);

            model.addAttribute("board", board);
            List<Comment> comments = commentRepository.findByBoardIdOrderByRegDateDesc(id);
            model.addAttribute("comments", comments);
        }
        return "view";
    }

    @GetMapping("/board/edit/{id}")
    public String editForm(Model model, @PathVariable("id") Long id, Principal principal) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board == null) return "redirect:/board/list";

        if (board.getPassword().equals("SECURED_MEMBER_POST")) {
            if (principal != null && principal.getName().equals(board.getWriter())) {
                model.addAttribute("board", board);
                return "edit";
            }
            return "redirect:/board/list?error=no_auth";
        }
        model.addAttribute("id", id);
        return "passCheck";
    }

    @PostMapping("/board/verify")
    public String verifyPassword(Long id, String password, Model model) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null && board.getPassword().equals(password)) {
            model.addAttribute("board", board);
            return "edit";
        }
        return "redirect:/board/list?error=wrong_pw";
    }

    @PostMapping("/board/update")
    public String update(Board board, @RequestParam("file") MultipartFile file,
                         @RequestParam(value="deleteFile", required=false) String deleteFile) throws IOException {

        Board oldBoard = boardRepository.findById(board.getId()).orElse(null);
        if (oldBoard == null) return "redirect:/board/list";

        oldBoard.setTitle(board.getTitle());
        oldBoard.setContent(board.getContent());
        oldBoard.setModDate(LocalDateTime.now());

        // 파일 수정 로직
        if (deleteFile != null || !file.isEmpty()) {
            if (oldBoard.getFileName() != null) {
                File oldFile = new File(uploadPath, oldBoard.getFileName());
                if (oldFile.exists()) oldFile.delete();
                oldBoard.setFileName(null);
                oldBoard.setFileOriginName(null);
            }
        }

        if (!file.isEmpty()) {
            String projectPath = System.getProperty("user.dir") + "/src/main/resources/static/files";

            // 폴더가 존재하는지 확인하고, 없으면 생성
            File dir = new File(projectPath);
            if (!dir.exists()) {
                dir.mkdirs(); // 하위 폴더까지 모두 생성
            }

            UUID uuid = UUID.randomUUID();
            String fileName = uuid + "_" + file.getOriginalFilename();

            File saveFile = new File(projectPath, fileName);
            file.transferTo(saveFile);

            board.setFileName(fileName);
            board.setFileOriginName(file.getOriginalFilename());
        }

        boardRepository.save(oldBoard);
        return "redirect:/board/view/" + board.getId();
    }

    // [수정됨] 삭제 폼 매핑 중복 해결 및 실제 파일 삭제 연동
    @GetMapping("/board/delete/{id}")
    public String deleteForm(@PathVariable("id") Long id, Model model, Principal principal) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board == null) return "redirect:/board/list";

        if (board.getPassword().equals("SECURED_MEMBER_POST")) {
            if (principal != null && principal.getName().equals(board.getWriter())) {
                // 삭제 전 파일 먼저 제거
                deleteActualFile(board);
                boardRepository.deleteById(id);
                return "redirect:/board/list";
            }
            return "redirect:/board/list?error=no_auth";
        }

        model.addAttribute("id", id);
        model.addAttribute("mode", "delete");
        return "passCheck";
    }

    @PostMapping("/board/delete_verify")
    public String deleteVerify(Long id, String password) {
        Board board = boardRepository.findById(id).orElse(null);
        if (board != null && board.getPassword().equals(password)) {
            deleteActualFile(board); // 실제 파일 삭제
            boardRepository.deleteById(id);
            return "redirect:/board/list";
        }
        return "redirect:/board/list?error=wrong_pw";
    }

    // 실제 파일 삭제 공통 메서드
    private void deleteActualFile(Board board) {
        if (board.getFileName() != null) {
            File file = new File(uploadPath, board.getFileName());
            if (file.exists()) file.delete();
        }
    }

    // --- [댓글 관련] ---

    @PostMapping("/board/comment/write")
    public String commentWrite(Long boardId, String content, String password,
                               String nickname, Principal principal,
                               HttpServletRequest request) {
        Board board = boardRepository.findById(boardId).orElse(null);
        if (board != null) {
            Comment comment = new Comment();
            comment.setBoard(board);
            comment.setContent(content);

            // [핵심] 회원/비회원 구분하여 모든 필드에 값을 채워야 합니다.
            if (principal != null) {
                // 1. 로그인 회원
                comment.setWriter(principal.getName()); // ID 저장
                comment.setWriterNickname(principal.getName()); // 닉네임으로 ID 사용
                comment.setPassword("SECURED_MEMBER_POST"); // 회원용 고정 비번 필수!
            } else {
                // 2. 비회원
                comment.setWriter(request.getRemoteAddr()); // IP 저장
                comment.setWriterNickname(nickname); // 입력받은 닉네임
                comment.setPassword(password); // 입력받은 비밀번호 (null이면 에러 발생함)
            }

            // password가 null이면 DB 에러가 나므로, 비회원일 때 password 비어있는지 체크
            if (comment.getPassword() == null || comment.getPassword().trim().isEmpty()) {
                return "redirect:/board/view/" + boardId + "?error=pw_required";
            }

            commentRepository.save(comment);
        }
        return "redirect:/board/view/" + boardId;
    }

    @PostMapping("/board/comment/delete/{id}")
    @ResponseBody
    public String commentDelete(@PathVariable("id") Long id, @RequestParam("password") String password, Principal principal) {
        Comment comment = commentRepository.findById(id).orElse(null);
        if (comment == null) return "fail";

        boolean canDelete = false;
        if ("SECURED_MEMBER_POST".equals(comment.getPassword())) {
            if (principal != null && principal.getName().equals(comment.getWriter())) canDelete = true;
        } else if (comment.getPassword().equals(password)) {
            canDelete = true;
        }

        if (canDelete) {
            commentRepository.deleteById(id);
            return "success";
        }
        return "pw_error";
    }

    @PostMapping("/board/comment/update")
    @ResponseBody
    public String commentUpdate(Long commentId, String content, String password, Principal principal) {
        Comment comment = commentRepository.findById(commentId).orElse(null);
        if (comment == null) return "fail";

        boolean canUpdate = false;
        if ("SECURED_MEMBER_POST".equals(comment.getPassword())) {
            if (principal != null && principal.getName().equals(comment.getWriter())) canUpdate = true;
        } else if (comment.getPassword().equals(password)) {
            canUpdate = true;
        }

        if (canUpdate) {
            comment.setContent(content);
            comment.setModDate(LocalDateTime.now());
            commentRepository.save(comment);
            return "success";
        }
        return "pw_error";
    }
}