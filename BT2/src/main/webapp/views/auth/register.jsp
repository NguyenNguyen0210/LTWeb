<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Đăng ký" />
<%@ include file="/views/inc/head.jspf" %>

<div class="auth-wrap">
    <div class="auth-card">
        <h1>Đăng ký tài khoản</h1>
        <p class="sub">Mã xác nhận OTP sẽ được gửi tới email của bạn</p>

        <c:if test="${not empty error}">
            <div class="alert alert-error">${fn:escapeXml(error)}</div>
        </c:if>

        <form method="post" action="${ctx}/register">
            <div class="field">
                <label for="fullname">Họ tên</label>
                <input type="text" id="fullname" name="fullname" value="${fn:escapeXml(fullname)}"
                       placeholder="Nguyễn Văn A" maxlength="100" required>
            </div>

            <div class="field">
                <label for="email">Email</label>
                <input type="text" id="email" name="email" value="${fn:escapeXml(email)}"
                       placeholder="you@example.com" autocomplete="email" required>
                <div class="hint">Dùng email thật để nhận được mã OTP.</div>
            </div>

            <div class="field">
                <label for="password">Mật khẩu</label>
                <input type="password" id="password" name="password"
                       placeholder="Ít nhất 6 ký tự" minlength="6" autocomplete="new-password" required>
                <div class="hint">Mật khẩu được mã hóa BCrypt trước khi lưu vào database.</div>
            </div>

            <div class="field">
                <label for="confirm">Nhập lại mật khẩu</label>
                <input type="password" id="confirm" name="confirm"
                       placeholder="Nhập lại mật khẩu" minlength="6" autocomplete="new-password" required>
            </div>

            <button class="btn btn-primary btn-block" type="submit">Đăng ký</button>
        </form>

        <div class="auth-links">
            <span></span>
            <a href="${ctx}/login">Đã có tài khoản? Đăng nhập</a>
        </div>
    </div>
</div>

<%@ include file="/views/inc/foot.jspf" %>
