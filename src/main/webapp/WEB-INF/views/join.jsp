<%--
  Created by IntelliJ IDEA.
  User: owner
  Date: 26. 1. 10.
  Time: 오후 7:53
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>회원가입 📝</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f8f9fa; }
        .join-container { margin-top: 60px; max-width: 500px; }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center">
    <div class="join-container w-100 mb-5">
        <div class="card shadow-lg border-0">
            <div class="card-body p-5">
                <div class="text-center mb-4">
                    <h2 class="fw-bold text-primary">회원가입</h2>
                    <p class="text-muted">새로운 계정을 만들어보세요!</p>
                </div>

                <form action="/member/join" method="post">
                    <div class="mb-3">
                        <label class="form-label fw-bold small">아이디</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white"><i class="bi bi-person"></i></span>
                            <input type="text" name="memberId" class="form-control" placeholder="아이디 입력" required>
                        </div>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-bold small">비밀번호</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white"><i class="bi bi-lock"></i></span>
                            <input type="password" name="password" class="form-control" placeholder="비밀번호 입력" required>
                        </div>
                    </div>

                    <div class="mb-4">
                        <label class="form-label fw-bold small">이름</label>
                        <div class="input-group">
                            <span class="input-group-text bg-white"><i class="bi bi-card-text"></i></span>
                            <input type="text" name="name" class="form-control" placeholder="실명 또는 닉네임" required>
                        </div>
                    </div>

                    <button type="submit" class="btn btn-primary w-100 py-3 fw-bold shadow-sm mb-3">
                        가입 완료하기
                    </button>

                    <div class="text-center small">
                        <span class="text-muted">이미 계정이 있으신가요?</span>
                        <a href="/member/login" class="text-decoration-none fw-bold ms-1">로그인</a>
                    </div>
                </form>
            </div>
        </div>
        <div class="text-center mt-4">
            <a href="/board/list" class="text-secondary text-decoration-none small">
                <i class="bi bi-house-door"></i> 게시판으로 돌아가기
            </a>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>
