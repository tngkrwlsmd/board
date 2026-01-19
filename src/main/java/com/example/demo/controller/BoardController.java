package com.example.demo.controller;

import com.example.demo.entity.Board;
import com.example.demo.entity.BoardFile;
import com.example.demo.entity.Comment;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.CommentRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${file.upload-dir}")
    private String uploadPath;

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
    public String write(Board board,
                        @RequestParam(value = "imageFiles", required = false) List<MultipartFile> imageFiles,
                        @RequestParam(value = "videoFiles", required = false) List<MultipartFile> videoFiles,
                        @RequestParam(value = "multipartFiles", required = false) List<MultipartFile> multipartFiles, // 이름 변경 및 필수 해제
                        Principal principal, HttpServletRequest request) throws IOException {

        if (principal != null) {
            // 1. 로그인 회원
            board.setWriter(principal.getName()); // ID 저장
            board.setWriterNickname(principal.getName()); // 닉네임으로 ID 사용
            board.setPassword("SECURED_MEMBER_POST"); // 회원용 고정 비번 필수!
        } else {
            // 2. 비회원
            board.setWriter(request.getRemoteAddr()); // IP 저장
        }

        File dir = new File(uploadPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (multipartFiles != null) { // 파일 리스트가 존재할 때만 실행
            for (MultipartFile file : multipartFiles) {
                if (!file.isEmpty()) {
                    UUID uuid = UUID.randomUUID();
                    String fileName = uuid + "_" + file.getOriginalFilename();
                    file.transferTo(new File(uploadPath, fileName));

                    BoardFile boardFile = new BoardFile();
                    boardFile.setFileName(fileName);
                    boardFile.setFileOriginName(file.getOriginalFilename());
                    boardFile.setBoard(board);
                    board.getFiles().add(boardFile);
                }
            }
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

            // 📝 본문 치환 로직 시작
            String content = board.getContent();
            List<BoardFile> files = board.getFiles();

            for (int i = 0; i < files.size(); i++) {
                String target = "[IMG_" + i + "]"; // 본문에서 찾을 표시
                String replacement = "<img src='/files/" + files.get(i).getFileName() + "' class='img-fluid'>"; // 바꿀 HTML 태그

                // 여기서 content 변수의 내용을 업데이트해야 합니다.
                content = content.replace(target, replacement);
            }

            model.addAttribute("convertedContent", content); // 변환된 본문을 모델에 담습니다.
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
    public String update(Board board,
                         @RequestParam(value="multipartFiles", required=false) List<MultipartFile> multipartFiles, // 이름 변경
                         @RequestParam(value="deleteFileIds", required=false) List<Long> deleteFileIds) throws IOException {

        // 1. 기존 게시글 정보 가져오기
        Board oldBoard = boardRepository.findById(board.getId()).orElse(null);
        if (oldBoard == null) return "redirect:/board/list";

        // 기본 정보 수정 (제목, 내용, 수정일)
        oldBoard.setTitle(board.getTitle());
        oldBoard.setContent(board.getContent());
        oldBoard.setModDate(LocalDateTime.now());

        // 2. 기존 파일 삭제 처리 (체크박스에 선택된 파일들)
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            // 리스트에서 요소를 삭제할 때는 removeIf를 사용하면 편리하고 안전합니다.
            oldBoard.getFiles().removeIf(oldFile -> {
                if (deleteFileIds.contains(oldFile.getId())) {
                    // 물리적 파일 삭제 (서버 폴더에서 제거)
                    File file = new File(uploadPath, oldFile.getFileName());
                    if (file.exists()) {
                        file.delete();
                    }
                    return true; // 리스트(논리적)에서도 삭제
                }
                return false;
            });
        }

        // 3. 새 파일 추가 업로드 처리
        if (multipartFiles != null && !multipartFiles.isEmpty()) {
            for (MultipartFile file : multipartFiles) {
                if (!file.isEmpty()) {
                    // 폴더 존재 확인 및 생성
                    File dir = new File(uploadPath);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }

                    UUID uuid = UUID.randomUUID();
                    String fileName = uuid + "_" + file.getOriginalFilename();

                    File saveFile = new File(uploadPath, fileName);
                    file.transferTo(saveFile);

                    // BoardFile 엔티티 생성 및 연결
                    BoardFile boardFile = new BoardFile();
                    boardFile.setFileName(fileName);
                    boardFile.setFileOriginName(file.getOriginalFilename());
                    boardFile.setBoard(oldBoard);

                    oldBoard.getFiles().add(boardFile);
                }
            }
        }

        // 4. 최종 저장
        boardRepository.save(oldBoard);
        return "redirect:/board/view/" + board.getId();
    }

    // 삭제 폼 매핑 중복 해결 및 실제 파일 삭제 연동
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
        // 1. 게시글에 연결된 파일 리스트가 비어있지 않은지 먼저 확인합니다.
        if (board.getFiles() != null && !board.getFiles().isEmpty()) {
            // 2. 리스트에서 BoardFile 객체를 하나씩 꺼내어 처리합니다.
            for (BoardFile file : board.getFiles()) {
                // 3. 물리적 파일 삭제 (저장된 UUID 이름을 사용합니다) 💾
                File physicalFile = new File(uploadPath, file.getFileName());
                if (physicalFile.exists()) {
                    physicalFile.delete();
                }
            }
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