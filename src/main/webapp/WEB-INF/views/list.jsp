<%--
  Created by IntelliJ IDEA.
  User: owner
  Date: 26. 1. 10.
  Time: 오후 7:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
    <title>게시판 목록</title>
    <style>
        /* 제목 클릭 시 포인터 변경 */
        .main-title { cursor: pointer; transition: 0.2s; }
        .main-title:hover { opacity: 0.8; }
    </style>
</head>
<body class="bg-light">
<div class="container mt-5 mb-5">
    <div class="card shadow border-0">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center p-3">
            <h2 class="mb-0 main-title fw-bold" onclick="location.href='/board/list'">
                <i class="bi bi-journal-text"></i> 게시글 목록 📋
            </h2>

            <div class="d-flex align-items-center gap-2">
                <a href="/board/list" class="btn btn-sm btn-light text-primary fw-bold">전체보기</a>

                <c:if test="${not empty pageContext.request.userPrincipal}">
                    <span class="ms-2"><b>${pageContext.request.userPrincipal.name}</b>님 환영합니다!
                        <form action="/member/logout" method="post" class="d-inline ms-2">
                            <button type="submit" class="btn btn-sm btn-outline-light">로그아웃</button>
                        </form>
                    </span>
                </c:if>
                <c:if test="${empty pageContext.request.userPrincipal}">
                    <div>
                        <a href="/member/login" class="btn btn-sm btn-outline-light">로그인</a>
                        <a href="/member/join" class="btn btn-sm btn-light">회원가입</a>
                    </div>
                </c:if>
            </div>
        </div>

        <div class="card-body p-4">
            <table class="table table-hover align-middle">
                <thead class="table-light">
                <tr>
                    <th style="width: 80px;">번호</th>
                    <th>제목</th>
                    <th style="width: 150px;">작성자</th>
                    <th style="width: 100px;">조회수</th>
                    <th style="width: 180px;">작성일</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="board" items="${list}" varStatus="status">
                    <tr>
                        <td><span class="text-muted">${list.size() - status.index}</span></td>
                        <td>
                            <a href="/board/view/${board.id}" class="text-decoration-none text-dark fw-bold">
                                    ${board.title}
                            </a>
                        </td>
                        <td>
                            <span class="badge bg-secondary opacity-75">
                                ${board.writerNickname != null ? board.writerNickname : board.writer}
                            </span>

                                <%-- 비회원 글일 경우 IP 일부 노출 (IPv6 로컬 환경 대응) --%>
                            <c:if test="${board.password ne 'SECURED_MEMBER_POST'}">
                                <small class="text-muted" style="font-size: 0.7rem;">
                                    <c:choose>
                                        <%-- IPv4 주소 (마침표가 있는 경우) --%>
                                        <c:when test="${fn:contains(board.writer, '.')}">
                                            (${fn:substringBefore(board.writer, '.')}.${fn:split(board.writer, '.')[1]})
                                        </c:when>
                                        <%-- IPv6 로컬 주소 (0:0...:1) --%>
                                        <c:when test="${board.writer eq '0:0:0:0:0:0:0:1'}">
                                            (127.0.1)
                                        </c:when>
                                        <%-- 기타 (이미 작성된 '익명' 등 데이터 대응) --%>
                                        <c:otherwise>
                                            <c:if test="${fn:length(board.writer) > 5}">
                                                (${fn:substring(board.writer, 0, 5)})
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </small>
                            </c:if>
                        </td>
                        <td>${board.viewCount}</td>
                        <td class="text-muted small">${board.regDate.toString().replace('T', ' ').substring(0, 16)}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty list}">
                    <tr>
                        <td colspan="5" class="text-center py-5 text-muted">게시글이 없습니다.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>

            <nav aria-label="Page navigation" class="my-4">
                <ul class="pagination justify-content-center shadow-sm">
                    <li class="page-item ${page.first ? 'disabled' : ''}">
                        <a class="page-link" href="?page=${page.number}&searchType=${param.searchType}&keyword=${param.keyword}" aria-label="Previous">
                            <span aria-hidden="true">&laquo; 이전</span>
                        </a>
                    </li>

                    <c:forEach var="i" begin="1" end="${page.totalPages}">
                        <li class="page-item ${page.number + 1 == i ? 'active' : ''}">
                            <a class="page-link" href="?page=${i}&searchType=${param.searchType}&keyword=${param.keyword}">${i}</a>
                        </li>
                    </c:forEach>

                    <li class="page-item ${page.last ? 'disabled' : ''}">
                        <a class="page-link" href="?page=${page.number + 2}&searchType=${param.searchType}&keyword=${param.keyword}" aria-label="Next">
                            <span aria-hidden="true">다음 &raquo;</span>
                        </a>
                    </li>
                </ul>
            </nav>

            <div class="row justify-content-center mt-4 mb-2">
                <div class="col-md-8">
                    <form action="/board/list" method="get" class="input-group shadow-sm">
                        <select name="searchType" class="form-select" style="max-width: 120px;">
                            <option value="title" ${param.searchType == 'title' ? 'selected' : ''}>제목</option>
                            <option value="content" ${param.searchType == 'content' ? 'selected' : ''}>내용</option>
                            <option value="writer" ${param.searchType == 'writer' ? 'selected' : ''}>작성자</option>
                        </select>
                        <input type="text" name="keyword" class="form-control" placeholder="검색어를 입력하세요..." value="${param.keyword}">
                        <button class="btn btn-primary" type="submit">
                            <i class="bi bi-search"></i> 검색
                        </button>
                    </form>
                </div>
            </div>

            <div class="text-end mt-3">
                <a href="/board/write" class="btn btn-primary px-4 shadow-sm">
                    <i class="bi bi-pencil-fill me-1"></i> 글쓰기
                </a>
            </div>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>