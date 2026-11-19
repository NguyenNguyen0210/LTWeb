<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Đặt lại mật khẩu</title></head>
<body>

<div class="auth-wrap">
    <div class="auth-card">
        <h1>Đặt lại mật khẩu</h1>
        <p class="sub">
            Nhập mã xác nhận đã gửi tới <strong>${fn:escapeXml(email)}</strong> và mật khẩu mới
        </p>

        <c:if test="${not empty error}">
            <div class="alert alert-error" role="alert">${fn:escapeXml(error)}</div>
        </c:if>

        <c:if test="${not mailConfigured}">
            <div class="alert alert-warn">
                Chưa cấu hình SMTP nên mã OTP đang được in ra console Tomcat
                (<code>catalina.out</code>).
            </div>
        </c:if>

        <form method="post" action="${ctx}/reset">
            <input type="hidden" name="email" value="${fn:escapeXml(email)}">

            <div class="field">
                <label for="otp">Mã xác nhận</label>
                <input type="text" id="otp" name="otp" class="otp-input" autocomplete="one-time-code"
                       inputmode="numeric" maxlength="6" pattern="[0-9]{6}"
                       placeholder="000000" autofocus required>
            </div>

            <div class="field">
                <label for="password">Mật khẩu mới</label>
                <input type="password" id="password" name="password" maxlength="72"
                       placeholder="Ít nhất 6 ký tự" minlength="6" autocomplete="new-password" required>
            </div>

            <div class="field">
                <label for="confirm">Nhập lại mật khẩu mới</label>
                <input type="password" id="confirm" name="confirm" maxlength="72"
                       placeholder="Nhập lại mật khẩu" minlength="6" autocomplete="new-password" required>
            </div>

            <button class="btn btn-primary btn-block" type="submit">Đổi mật khẩu</button>
        </form>

        <div class="auth-links">
            <a href="${ctx}/forgot">Gửi lại mã khác</a>
            <a href="${ctx}/login">Về đăng nhập</a>
        </div>
    </div>
</div>

</body>
</html>
