<%--
  Created by IntelliJ IDEA.
  User: owner
  Date: 26. 1. 10.
  Time: 오후 7:20
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
  <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
  <title>게시판 서비스</title>
</head>
<body class="bg-light">

<div class="container mt-5 mb-5">
  <div class="card shadow border-0">
    <div class="card-body p-5">
      <h1 class="border-bottom pb-3 mb-4 fw-bold text-dark"
          style="cursor: pointer; transition: 0.2s;"
          onclick="location.href='/board/list'"
          onmouseover="this.style.opacity='0.7'"
          onmouseout="this.style.opacity='1'"
          title="목록으로 돌아가기">
        ${board.title}
      </h1>

      <div class="d-flex justify-content-between text-muted mb-4 small bg-light p-2 rounded">
        <span>
          <i class="bi bi-person-fill"></i> 작성자:
          <b>${board.writerNickname != null ? board.writerNickname : board.writer}</b>

          <c:if test="${board.password ne 'SECURED_MEMBER_POST'}">
            <span class="small text-muted fw-normal">
              <c:choose>
                <c:when test="${fn:contains(board.writer, '.')}">
                  (${fn:substringBefore(board.writer, '.')}.${fn:split(board.writer, '.')[1]})
                </c:when>
                <c:when test="${board.writer eq '0:0:0:0:0:0:0:1'}">
                  (127.0.1)
                </c:when>
                <c:otherwise>
                  <c:if test="${fn:length(board.writer) > 5}">(${fn:substring(board.writer, 0, 5)})</c:if>
                </c:otherwise>
              </c:choose>
            </span>
          </c:if>
        </span>
        <span>
          <i class="bi bi-calendar-event"></i> 작성일: ${board.regDate.toString().replace('T', ' ').substring(0, 16)}

          <%-- modDate가 존재하고, 작성일과 다를 때만 '수정됨' 표시 --%>
          <c:if test="${not empty board.modDate}">
            <span class="text-danger ms-2" style="font-size: 0.85rem;">
              (수정됨: ${board.modDate.toString().replace('T', ' ').substring(5, 16).replace('T', ' ')})
            </span>
          </c:if>
        </span>
      </div>

      <%-- 기존의 ${board.content} 대신 아래 코드를 넣어주세요 --%>
      <div class="p-4 bg-white border rounded mb-5" style="min-height: 300px; white-space: pre-wrap;">
        <c:out value="${convertedContent}" escapeXml="false" />
      </div>

      <%-- 📎 첨부파일 리스트 영역 수정됨 --%>
      <c:if test="${not empty board.files}">
        <div class="mt-3 p-3 border rounded bg-light">
          <h6 class="fw-bold mb-2"><i class="bi bi-paperclip"></i> 첨부파일</h6>
          <ul class="list-unstyled mb-0">
            <c:forEach var="file" items="${board.files}">
              <li class="mb-1">
                <a href="/files/${file.fileName}" download="${file.fileOriginName}" class="text-decoration-none">
                  <i class="bi bi-file-earmark-arrow-down"></i> ${file.fileOriginName}
                </a>
              </li>
            </c:forEach>
          </ul>
        </div>
      </c:if>

      <div class="bg-light p-4 rounded mb-4 mt-4">
        <h5 class="mb-3 fw-bold"><i class="bi bi-chat-dots-fill"></i> 댓글 💬</h5>

        <form action="/board/comment/write" method="post" class="mb-4">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
          <input type="hidden" name="boardId" value="${board.id}">

          <c:if test="${empty pageContext.request.userPrincipal}">
            <div class="row g-2 mb-2">
              <div class="col-md-3">
                <input type="text" name="nickname" class="form-control form-control-sm" placeholder="닉네임" required>
              </div>
              <div class="col-md-3">
                <input type="password" name="password" class="form-control form-control-sm" placeholder="비밀번호" required>
              </div>
            </div>
          </c:if>

          <div class="input-group shadow-sm">
            <textarea name="content" class="form-control" rows="2" placeholder="댓글을 남겨주세요" required></textarea>
            <button class="btn btn-primary px-4" type="submit">등록</button>
          </div>
        </form>

        <div class="comment-list">
          <c:forEach var="comment" items="${comments}">
            <div class="mb-3 pb-3 border-bottom">
              <div class="d-flex justify-content-between align-items-center mb-1">
              <span class="fw-bold">
                <i class="bi bi-person-circle me-1"></i>
                ${comment.writerNickname}

                <c:if test="${comment.password ne 'SECURED_MEMBER_POST'}">
                  <span class="text-muted small fw-normal">
                    <c:choose>
                      <c:when test="${fn:contains(comment.writer, '.')}">
                        (${fn:substringBefore(comment.writer, '.')}.${fn:split(comment.writer, '.')[1]})
                      </c:when>
                      <c:when test="${comment.writer eq '0:0:0:0:0:0:0:1'}">
                        (127.0.1)
                      </c:when>
                      <c:otherwise>
                        <c:if test="${fn:length(comment.writer) > 5}">(${fn:substring(comment.writer, 0, 5)})</c:if>
                      </c:otherwise>
                    </c:choose>
                  </span>
                </c:if>
              </span>
                <small class="text-muted">
                    ${comment.regDate.toString().substring(5,16).replace('T', ' ')}
                </small>
              </div>

              <div class="d-flex justify-content-between align-items-start">
                <p class="mb-0 text-secondary">
                    ${comment.content}
                  <c:if test="${comment.modDate != null}">
                    <small class="text-muted ms-1" style="font-size: 0.75rem;">(수정됨)</small>
                  </c:if>
                </p>

                <div class="btn-group">
                  <c:choose>
                    <c:when test="${comment.password eq 'SECURED_MEMBER_POST'}">
                      <c:if test="${not empty pageContext.request.userPrincipal and pageContext.request.userPrincipal.name eq comment.writer}">
                        <button onclick="editComment('${comment.id}', '${comment.content}')" class="btn btn-link btn-sm text-decoration-none p-0 me-2">수정</button>
                        <a href="/board/comment/delete/${comment.id}" class="btn btn-link btn-sm text-danger text-decoration-none p-0" onclick="return confirm('삭제할까요?')">삭제</a>
                      </c:if>
                    </c:when>
                    <c:otherwise>
                      <button onclick="editComment('${comment.id}', '${comment.content}')" class="btn btn-link btn-sm text-decoration-none p-0 me-2">수정</button>
                      <button onclick="deleteComment('${comment.id}')" class="btn btn-link btn-sm text-warning text-decoration-none p-0">삭제</button>
                    </c:otherwise>
                  </c:choose>
                </div>
              </div>
            </div>
          </c:forEach>
        </div>
      </div>

      <div class="text-center mt-5 border-top pt-4">
        <a href="/board/list" class="btn btn-outline-secondary px-4 me-2"><i class="bi bi-list"></i> 목록</a>

        <c:set var="isMemberPost" value="${board.password eq 'SECURED_MEMBER_POST'}" />

        <c:choose>
          <c:when test="${not empty pageContext.request.userPrincipal}">
            <c:if test="${isMemberPost && pageContext.request.userPrincipal.name eq board.writer}">
              <a href="/board/edit/${board.id}" class="btn btn-warning px-4 me-2"><i class="bi bi-pencil-square"></i> 수정</a>
              <a href="/board/delete/${board.id}" class="btn btn-danger px-4" onclick="return confirm('정말 삭제하시겠습니까?')"><i class="bi bi-trash"></i> 삭제</a>
            </c:if>
          </c:when>
          <c:otherwise>
            <c:if test="${!isMemberPost}">
              <a href="/board/edit/${board.id}" class="btn btn-warning px-4 me-2"><i class="bi bi-pencil-square"></i> 수정</a>
              <a href="/board/delete/${board.id}" class="btn btn-danger px-4"><i class="bi bi-trash"></i> 삭제</a>
            </c:if>
          </c:otherwise>
        </c:choose>
      </div>
      <div class="text-end mt-3">
        <a href="/board/write" class="btn btn-primary px-4 shadow-sm">
          <i class="bi bi-pencil-fill me-1"></i> 글쓰기
        </a>
      </div>
    </div>
  </div>
</div>

<c:if test="${param.commentError eq 'pw'}">
  <script>alert("댓글 비밀번호가 일치하지 않습니다.");</script>
</c:if>

<script>
  function deleteComment(commentId) {
    document.getElementById('modalCommentId').value = commentId;
    document.getElementById('modalCommentPw').value = "";
    const myModal = new bootstrap.Modal(document.getElementById('deleteCommentModal'));
    myModal.show();
  }

  function confirmDeleteAjax() {
    const id = document.getElementById('modalCommentId').value;
    const pw = document.getElementById('modalCommentPw').value;
    if (!pw) { alert("비밀번호를 입력해주세요."); return; }
    const params = "password=" + encodeURIComponent(pw);
    fetch("/board/comment/delete/" + id, {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: params
    }).then(res => res.text()).then(data => {
      if (data === "success") { location.reload(); }
      else if (data === "pw_error") { alert("비밀번호가 틀렸습니다."); }
      else { alert("삭제 권한이 없거나 오류가 발생했습니다."); }
    });
  }

  function editComment(commentId, currentContent) {
    const newContent = prompt("수정할 내용을 입력하세요.", currentContent);
    if (!newContent) return;
    let pw = "";
    <c:if test="${empty pageContext.request.userPrincipal}">
    pw = prompt("비밀번호를 입력하세요.");
    if(!pw) return;
    </c:if>
    const params = "commentId=" + commentId + "&content=" + encodeURIComponent(newContent) + "&password=" + pw;
    fetch("/board/comment/update", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body: params
    }).then(res => res.text()).then(data => {
      if(data === "success") { location.reload(); }
      else if(data === "pw_error") { alert("비밀번호가 틀렸습니다."); }
      else { alert("수정 권한이 없거나 오류가 발생했습니다."); }
    });
  }
</script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<div class="modal fade" id="deleteCommentModal" tabindex="-1" aria-hidden="true">
  <div class="modal-dialog modal-dialog-centered modal-sm">
    <div class="modal-content shadow">
      <div class="modal-header bg-danger text-white">
        <h5 class="modal-title small"><i class="bi bi-shield-lock"></i> 댓글 삭제 확인</h5>
        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="modal" aria-label="Close"></button>
      </div>
      <div class="modal-body text-center">
        <p class="small text-muted mb-3">작성 시 설정한 비밀번호를 입력하세요.</p>
        <input type="password" id="modalCommentPw" class="form-control text-center" placeholder="비밀번호">
        <input type="hidden" id="modalCommentId">
      </div>
      <div class="modal-footer justify-content-center border-0 pt-0">
        <button type="button" class="btn btn-secondary btn-sm px-3" data-bs-dismiss="modal">취소</button>
        <button type="button" class="btn btn-danger btn-sm px-3" onclick="confirmDeleteAjax()">삭제</button>
      </div>
    </div>
  </div>
</div>
</body>
</html>