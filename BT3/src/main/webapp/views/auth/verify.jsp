<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head><title>Kích hoạt tài khoản</title></head>
<body>

<div class="auth-wrap">
    <div class="auth-card">
        <h1>Kích hoạt tài khoản</h1>
        <p class="sub">
            Nhập mã gồm 6 chữ số đã gửi tới
            <strong>${fn:escapeXml(email)}</strong>
        </p>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${fn:escapeXml(error)}</div>
        </c:if>

        <c:if test="${not mailConfigured}">
            <div class="alert alert-warn">
                Chưa cấu hình SMTP (<code>mail.properties</code>) nên mã OTP đang được in ra
                console Tomcat (<code>catalina.out</code>). Điền <code>mail.username</code> /
                <code>mail.password</code> để gửi mail thật.
            </div>
        </c:if>

        <form method="post" action="${ctx}/verify">
            <input type="hidden" name="email" value="${fn:escapeXml(email)}">

            <div class="field">
                <label for="otp">Mã xác nhận</label>
                <input type="text" id="otp" name="otp" class="otp-input"
                       inputmode="numeric" maxlength="6" pattern="[0-9]{6}"
                       placeholder="000000" autofocus required>
                <div class="hint">Mã có hiệu lực 5 phút.</div>
            </div>

            <button class="btn btn-primary btn-block" type="submit">Xác nhận</button>
        </form>

        <form method="post" action="${ctx}/verify" class="resend-form">
            <input type="hidden" name="email" value="${fn:escapeXml(email)}">
            <input type="hidden" name="action" value="resend">
            <button class="btn btn-light btn-block" type="submit">Gửi lại mã OTP</button>
        </form>

        <div class="auth-links">
            <a href="${ctx}/register">Đổi email khác</a>
            <a href="${ctx}/login">Về trang đăng nhập</a>
        </div>
    </div>
</div>

</body>
</html>
