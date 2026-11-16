<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Trang chủ" />
<%@ include file="/views/inc/head.jspf" %>

<section class="hero">
    <div class="hero-text">
        <span class="pill">JPA · Hibernate 7 · Jakarta EE 11</span>
        <h1>Chia sẻ video của bạn</h1>
        <p>
            Đăng ký tài khoản bằng email, kích hoạt bằng OTP, đăng nhập an toàn với mật khẩu
            mã hóa BCrypt và quản lý video cá nhân.
        </p>
        <div class="hero-actions">
            <c:choose>
                <c:when test="${empty sessionScope.user}">
                    <a class="btn btn-primary" href="${ctx}/register">Tạo tài khoản</a>
                    <a class="btn btn-light" href="${ctx}/login">Đăng nhập</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-primary" href="${ctx}/video">Video của tôi</a>
                    <a class="btn btn-light" href="${ctx}/video/add">+ Thêm video</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</section>

<section class="section">
    <div class="section-head">
        <h2>10 video mới nhất</h2>
        <c:if test="${not empty sessionScope.user}">
            <a class="link-more" href="${ctx}/video">Xem video của tôi →</a>
        </c:if>
    </div>

    <c:choose>
        <c:when test="${empty latestVideos}">
            <div class="empty">Chưa có video nào. Hãy đăng nhập và thêm video đầu tiên!</div>
        </c:when>
        <c:otherwise>
            <div class="video-grid">
                <c:forEach items="${latestVideos}" var="v">
                    <article class="video-card">
                        <a class="thumb-link" href="${ctx}/video/detail?id=${v.videoId}">
                            <c:choose>
                                <c:when test="${empty v.poster}">
                                    <div class="poster poster-empty">▶</div>
                                </c:when>
                                <c:when test="${fn:startsWith(v.poster, 'http')}">
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
                                <span>${fn:escapeXml(v.user.displayName)}</span>
                                <span>·</span>
                                <span>${v.views} lượt xem</span>
                            </div>
                            <div class="video-meta muted">${v.createdAtText}</div>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</section>

<%@ include file="/views/inc/foot.jspf" %>
