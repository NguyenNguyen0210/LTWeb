<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <title>Lỗi - LTWeb</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>

<div class="error-box">
    <div class="code">${empty requestScope['jakarta.servlet.error.statusCode'] ? 500 : requestScope['jakarta.servlet.error.statusCode']}</div>
    <h2>Đã xảy ra lỗi</h2>
    <p>
        <c:choose>
            <c:when test="${not empty requestScope['jakarta.servlet.error.message']}">
                ${fn:escapeXml(requestScope['jakarta.servlet.error.message'])}
            </c:when>
            <c:otherwise>Không tìm thấy trang hoặc máy chủ gặp sự cố.</c:otherwise>
        </c:choose>
    </p>
    <p class="muted">
        Nếu là lỗi kết nối cơ sở dữ liệu: kiểm tra SQL Server đang chạy, database
        <strong>webst3</strong> đã tồn tại và user/mật khẩu trong
        <code>META-INF/persistence.xml</code> đúng.
    </p>
    <a class="btn btn-primary" href="${ctx}/home">Về trang chủ</a>
</div>

</body>
</html>
