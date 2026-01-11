<%--
  Created by IntelliJ IDEA.
  User: owner
  Date: 26. 1. 10.
  Time: 오후 6:52
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>새 글 쓰기 ✍️</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        .main-title { cursor: pointer; transition: 0.2s; }
        .main-title:hover { opacity: 0.8; }
    </style>
</head>
<body class="bg-light">

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow-lg border-0">
                <div class="card-header bg-primary text-white p-3">
                    <h4 class="mb-0 main-title fw-bold" onclick="location.href='/board/list'">
                        <i class="bi bi-pencil-fill"></i> 새 글 작성하기
                    </h4>
                </div>

                <div class="card-body p-4">
                    <form action="/board/write" method="post" enctype="multipart/form-data">

                        <div class="form-floating mb-3">
                            <input type="text" name="title" class="form-control" id="title" placeholder="제목" required>
                            <label for="title">제목을 입력하세요</label>
                        </div>

                        <c:if test="${empty pageContext.request.userPrincipal}">
                            <div class="row g-2 mb-3">
                                <div class="col-md">
                                    <div class="form-floating">
                                        <input type="text" name="writerNickname" class="form-control" id="writerNickname" placeholder="작성자" required>
                                        <label for="writerNickname">작성자 닉네임</label>
                                    </div>
                                </div>
                                <div class="col-md">
                                    <div class="form-floating">
                                        <input type="password" name="password" class="form-control" id="password" placeholder="비밀번호" required>
                                        <label for="password">비밀번호</label>
                                    </div>
                                </div>
                            </div>
                        </c:if>

                        <div class="mb-3">
                            <label class="form-label fw-bold small"><i class="bi bi-paperclip"></i> 파일 첨부</label>
                            <input type="file" name="file" class="form-control">
                        </div>

                        <div class="form-floating mb-4">
                            <textarea name="content" class="form-control" id="content" placeholder="내용" style="height: 300px" required></textarea>
                            <label for="content">내용을 입력하세요</label>
                        </div>

                        <div class="d-flex justify-content-between border-top pt-4">
                            <a href="/board/list" class="btn btn-outline-secondary px-4">취소</a>
                            <button type="submit" class="btn btn-primary px-5 fw-bold">등록하기</button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>