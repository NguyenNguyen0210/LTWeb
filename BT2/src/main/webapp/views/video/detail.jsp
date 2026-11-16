<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Chi tiết video" />
<%@ include file="/views/inc/head.jspf" %>

<c:if test="${not empty error}">
    <div class="alert alert-error">${fn:escapeXml(error)}</div>
</c:if>

<div class="card">
    <div class="card-body">
        <div class="player">
            <c:choose>
                <c:when test="${video.youtube}">
                    <iframe src="${video.youtubeEmbedUrl}"
                            title="${fn:escapeXml(video.title)}"
                            allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
                            allowfullscreen></iframe>
                </c:when>
                <c:when test="${video.external}">
                    <video controls preload="metadata" poster="${video.posterUrl ? video.poster : ''}">
                        <source src="${video.videoUrl}">
                        Trình duyệt không hỗ trợ thẻ video.
                    </video>
                </c:when>
                <c:when test="${video.uploaded}">
                    <video controls preload="metadata">
                        <source src="${ctx}/media?fname=${video.videoUrl}">
                        Trình duyệt không hỗ trợ thẻ video.
                    </video>
                </c:when>
                <c:otherwise>
                    <div class="player-empty">Video này chưa có nguồn phát</div>
                </c:otherwise>
            </c:choose>
        </div>

        <div class="detail-head">
            <h1>${fn:escapeXml(video.title)}</h1>
            <div class="detail-meta">
                <span>${fn:escapeXml(video.user.displayName)}</span>
                <span>·</span>
                <span>${video.views} lượt xem</span>
                <span>·</span>
                <span>${video.createdAtText}</span>
                <c:if test="${not empty video.category}">
                    <span>·</span>
                    <span>${fn:escapeXml(video.category.categoryname)}</span>
                </c:if>
                <c:if test="${video.active != 1}">
                    <span class="badge badge-off">Đang ẩn</span>
                </c:if>
            </div>
        </div>

        <c:if test="${not empty video.description}">
            <div class="detail-desc">${fn:escapeXml(video.description)}</div>
        </c:if>

        <div class="detail-actions">
            <a class="btn btn-light" href="${ctx}/home">‹ Trang chủ</a>
            <c:if test="${not empty sessionScope.user}">
                <a class="btn btn-light" href="${ctx}/video">Video của tôi</a>
            </c:if>
            <c:if test="${canEdit}">
                <a class="btn btn-primary" href="${ctx}/video/edit?id=${video.videoId}">Sửa video</a>
                <a class="btn btn-danger" href="${ctx}/video/delete?id=${video.videoId}"
                   onclick="return confirm('Xóa video này ?');">Xóa video</a>
            </c:if>
        </div>
    </div>
</div>

<%@ include file="/views/inc/foot.jspf" %>
