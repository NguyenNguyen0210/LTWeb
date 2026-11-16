<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Đăng nhập" />
<%@ include file="/views/inc/head.jspf" %>

<div class="auth-wrap">
    <div class="auth-card">
        <h1>Đăng nhập</h1>
        <p class="sub">Dùng email và mật khẩu đã đăng ký</p>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${fn:escapeXml(error)}</div>
        </c:if>

        <form method="post" action="${ctx}/login">
            <div class="field">
                <label for="email">Email</label>
                <input type="text" id="email" name="email" value="${fn:escapeXml(email)}"
                       placeholder="you@example.com" autocomplete="username" required>
            </div>

            <div class="field">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password"
                       placeholder="••••••" autocomplete="current-password" required>
            </div>

            <button class="btn btn-primary btn-block" type="submit">Đăng nhập</button>
        </form>

        <div class="auth-links">
            <a href="${ctx}/forgot">Quên mật khẩu?</a>
            <a href="${ctx}/register">Chưa có tài khoản? Đăng ký</a>
        </div>
    </div>
</div>

<%@ include file="/views/inc/foot.jspf" %>
