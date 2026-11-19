<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Video của tôi</title></head>
<body>

<div class="card">
    <div class="card-head">
        <div>
            <h1>Video của tôi</h1>
            <p>
                Tổng <strong>${total}</strong> video
                <c:if test="${empty keyword and totalPages > 0}">
                    · Trang ${currentPage}/${totalPages}
                </c:if>
                <c:if test="${not empty keyword}">
                    · Kết quả cho "<strong>${fn:escapeXml(keyword)}</strong>"
                </c:if>
            </p>
        </div>

        <div style="display:flex;gap:10px;align-items:center;flex-wrap:wrap">
            <form class="search" method="get" action="${ctx}/video">
                <input type="text" name="keyword" value="${fn:escapeXml(keyword)}" placeholder="Tìm theo tiêu đề...">
                <button class="btn btn-light" type="submit">Tìm</button>
                <c:if test="${not empty keyword}">
                    <a class="btn btn-light" href="${ctx}/video">Bỏ lọc</a>
                </c:if>
            </form>
            <a class="btn btn-primary" href="${ctx}/video/add">+ Thêm video</a>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty listvideo}">
            <div class="empty">
                Bạn chưa có video nào. Bấm <strong>+ Thêm video</strong> để đăng video đầu tiên.
            </div>
        </c:when>
        <c:otherwise>
            <div class="card-body">
                <div class="video-grid">
                    <c:forEach items="${listvideo}" var="v">
                        <article class="video-card">
                            <a class="thumb-link" href="${ctx}/video/detail?id=${v.videoId}">
                                <c:choose>
                                    <c:when test="${empty v.poster}">
                                        <div class="poster poster-empty">▶</div>
                                    </c:when>
                                    <c:when test="${v.posterUrl}">
                                        <img class="poster" src="${v.poster}" alt="${fn:escapeXml(v.title)}">
                                    </c:when>
                                    <c:otherwise>
                                        <img class="poster" src="${ctx}/media?fname=${v.poster}"
                                             alt="${fn:escapeXml(v.title)}">
                                    </c:otherwise>
                                </c:choose>
                            </a>
                            <div class="video-body">
                                <h3>
                                    <a href="${ctx}/video/detail?id=${v.videoId}">${fn:escapeXml(v.title)}</a>
                                </h3>
                                <div class="video-meta">
                                    <span>${v.views} lượt xem</span>
                                    <span>·</span>
                                    <span>${v.createdAtText}</span>
                                </div>
                                <div class="row-actions" style="margin-top:8px">
                                    <a class="btn btn-light btn-sm" href="${ctx}/video/edit?id=${v.videoId}">Sửa</a>
                                    <a class="btn btn-danger btn-sm"
                                       href="${ctx}/video/delete?id=${v.videoId}"
                                       onclick="return confirm('Xóa video &quot;${fn:escapeXml(v.title)}&quot; ?');">Xóa</a>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </div>
        </c:otherwise>
    </c:choose>

    <c:if test="${empty keyword and totalPages > 1}">
        <div class="pager">
            <c:if test="${currentPage > 1}">
                <a href="${ctx}/video?page=${currentPage - 1}">‹ Trước</a>
            </c:if>

            <c:forEach begin="1" end="${totalPages}" var="i">
                <c:choose>
                    <c:when test="${i == currentPage}">
                        <span class="cur">${i}</span>
                    </c:when>
                    <c:otherwise>
                        <a href="${ctx}/video?page=${i}">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

            <c:if test="${currentPage < totalPages}">
                <a href="${ctx}/video?page=${currentPage + 1}">Sau ›</a>
            </c:if>

            <span class="info">6 video / trang</span>
        </div>
    </c:if>
</div>

</body>
</html>
