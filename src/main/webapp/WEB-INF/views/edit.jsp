<%--
  Created by IntelliJ IDEA.
  User: owner
  Date: 26. 1. 10.
  Time: 오후 7:23
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>글 수정하기 ✏️</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        .main-title { cursor: pointer; transition: 0.2s; }
        .main-title:hover { opacity: 0.8; }
        .border-dashed { border: 2px dashed #dee2e6 !important; }
        .extra-small { font-size: 0.75rem; }
    </style>
</head>
<body class="bg-light">

<div class="container mt-5 mb-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card shadow border-0">
                <div class="card-header bg-warning text-dark p-3">
                    <h4 class="mb-0 fw-bold main-title" onclick="location.href='/board/list'">
                        <i class="bi bi-pencil-square"></i> 게시글 수정
                    </h4>
                </div>

                <div class="card-body p-4">
                    <form action="/board/update" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                        <input type="hidden" name="id" value="${board.id}">

                        <div class="form-floating mb-3">
                            <input type="text" name="title" class="form-control" id="title" value="${board.title}" required>
                            <label for="title">제목</label>
                        </div>

                        <div class="mb-4">
                            <label class="form-label fw-bold small"><i class="bi bi-paperclip"></i> 첨부파일 관리</label>
                            <div class="card bg-light border-dashed">
                                <div class="card-body">
                                    <div class="mb-3">
                                        <label class="form-label fw-bold small"><i class="bi bi-paperclip"></i> 파일 첨부</label>
                                        <input type="file" name="multipartFiles" class="form-control" multiple>
                                    </div>
                                    <c:choose>
                                        <%-- 1. 기존 파일 목록 출력 --%>
                                        <c:when test="${not empty board.files}">
                                            <c:forEach var="file" items="${board.files}">
                                                <div class="d-flex align-items-center justify-content-between mb-2 p-2 bg-white rounded border">
                                                    <div class="text-truncate me-2">
                                                        <span class="badge bg-primary me-2">기존 파일</span>
                                                        <span class="text-dark small fw-bold">${file.fileOriginName}</span>
                                                    </div>
                                                        <%-- 삭제할 파일의 ID를 서버로 전송할 체크박스 --%>
                                                    <div class="form-check form-switch">
                                                        <input class="form-check-input" type="checkbox" name="deleteFileIds" value="${file.id}" id="deleteFile_${file.id}">
                                                        <label class="form-check-label small text-danger" for="deleteFile_${file.id}">삭제</label>
                                                    </div>
                                                </div>
                                            </c:forEach>
                                        </c:when>
                                        <c:otherwise>
                                            <p class="text-muted small mb-2">첨부된 파일이 없습니다.</p>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>

                        <div class="form-floating mb-4">
                            <textarea name="content" class="form-control" id="content" style="height: 300px" required>${board.content}</textarea>
                            <label for="content">내용</label>
                        </div>

                        <div class="d-flex justify-content-between border-top pt-4">
                            <a href="javascript:history.back()" class="btn btn-outline-secondary px-4">취소</a>
                            <button type="submit" class="btn btn-warning px-5 fw-bold">수정 완료</button>
                        </div>
                    </form> </div>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>