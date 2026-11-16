<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Quên mật khẩu" />
<%@ include file="/views/inc/head.jspf" %>

<div class="auth-wrap">
    <div class="auth-card">
        <h1>Quên mật khẩu</h1>
        <p class="sub">Nhập email đã đăng ký, hệ thống sẽ gửi mã xác nhận OTP</p>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${fn:escapeXml(error)}</div>
        </c:if>

        <form method="post" action="${ctx}/forgot">
            <div class="field">
                <label for="email">Email</label>
                <input type="text" id="email" name="email" value="${fn:escapeXml(email)}"
                       placeholder="you@example.com" autocomplete="email" required>
            </div>

            <button class="btn btn-primary btn-block" type="submit">Gửi mã xác nhận</button>
        </form>

        <div class="auth-links">
            <a href="${ctx}/login">‹ Về đăng nhập</a>
            <a href="${ctx}/register">Đăng ký tài khoản</a>
        </div>
    </div>
</div>

<%@ include file="/views/inc/foot.jspf" %>
