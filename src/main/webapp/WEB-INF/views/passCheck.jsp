<%--
  Created by IntelliJ IDEA.
  User: owner
  Date: 26. 1. 10.
  Time: 오후 8:55
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>비밀번호 확인 🔒</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <style>
        body { background-color: #f8f9fa; }
        .check-container { margin-top: 150px; max-width: 400px; }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center">
    <div class="check-container w-100">
        <div class="card shadow-sm border-0">
            <div class="card-body p-5 text-center">
                <div class="mb-4">
                    <i class="bi bi-shield-lock text-warning" style="font-size: 3rem;"></i>
                </div>
                <h4 class="fw-bold mb-3">비밀번호 확인</h4>
                <p class="text-muted small mb-4">
                    글을 ${mode eq 'delete' ? '삭제' : '수정'}하기 위해<br>작성 시 입력한 비밀번호를 입력해주세요.
                </p>

                <%-- mode에 따라 전송 주소를 다르게 설정 (수정 시 verify, 삭제 시 delete_verify) --%>
                <form action="/board/${mode eq 'delete' ? 'delete_verify' : 'verify'}" method="post">
                    <input type="hidden" name="id" value="${id}">

                    <div class="mb-4">
                        <input type="password" name="password" class="form-control form-control-lg text-center"
                               placeholder="••••••••" required autofocus>
                    </div>

                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-dark py-2 fw-bold">확인</button>
                        <button type="button" onclick="history.back()" class="btn btn-outline-secondary py-2">취소</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

</body>
</html>