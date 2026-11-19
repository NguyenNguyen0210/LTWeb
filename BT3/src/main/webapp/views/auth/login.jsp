<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Đăng nhập</title></head>
<body>

<div class="auth-wrap">
    <div class="auth-card">
        <h1>Đăng nhập</h1>
        <p class="sub">Dùng email và mật khẩu đã đăng ký</p>

        <c:if test="${not empty error}">
            <div class="alert alert-error" role="alert">${fn:escapeXml(error)}</div>
        </c:if>

        <form method="post" action="${ctx}/login">
            <div class="field">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" maxlength="100" value="${fn:escapeXml(email)}"
                       placeholder="you@example.com" autocomplete="username" required>
            </div>

            <div class="field">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password" maxlength="72"
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

</body>
</html>
