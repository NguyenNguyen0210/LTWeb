<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Trang chủ | LTWeb</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f4f6fb; color: #1f2937; padding: 24px;
        }
        .card {
            background: #fff; width: 100%; max-width: 560px; padding: 40px 36px;
            border-radius: 16px; box-shadow: 0 10px 30px rgba(15, 23, 42, .08);
            border-top: 6px solid #00539b;
        }
        .brand { font-size: 13px; letter-spacing: .08em; text-transform: uppercase; color: #00539b; font-weight: 700; }
        h1 { margin: 10px 0 6px; font-size: 28px; }
        p { line-height: 1.6; color: #4b5563; margin: 8px 0; }
        code { background: #eef2ff; color: #3730a3; padding: 2px 6px; border-radius: 4px; font-size: 13px; }
        .ok { background: #ecfdf5; border: 1px solid #a7f3d0; color: #065f46; padding: 14px 16px; border-radius: 10px; margin: 18px 0; }
        .btn {
            display: inline-block; margin-top: 20px; padding: 12px 26px; border-radius: 10px;
            background: #00539b; color: #fff; text-decoration: none; font-weight: 600; font-size: 15px;
            transition: background .2s;
        }
        .btn:hover { background: #003f75; }
        .btn-ghost { background: #e5e7eb; color: #1f2937; margin-left: 8px; }
        .btn-ghost:hover { background: #d1d5db; }
        .hint { margin-top: 28px; padding-top: 16px; border-top: 1px dashed #e5e7eb; font-size: 13px; color: #9ca3af; }
    </style>
</head>
<body>
<div class="card">
    <div class="brand">HCMUTE &middot; LTWeb</div>
    <h1>Trang chủ</h1>
    <p>Đây là nội dung của <code>index.jsp</code> &mdash; trang chủ của project.</p>

    <c:choose>
        <c:when test="${not empty sessionScope.user}">
            <div class="ok">
                Xin chào <strong><c:out value="${sessionScope.user.fullName}"/></strong>
                (<c:out value="${sessionScope.user.username}"/>) &mdash; bạn đã đăng nhập thành công.
            </div>
            <a class="btn" href="${pageContext.request.contextPath}/home">Vào trang /home</a>
            <a class="btn btn-ghost" href="${pageContext.request.contextPath}/login?action=logout">Đăng xuất</a>
        </c:when>
        <c:otherwise>
            <p>Bạn chưa đăng nhập. Bấm nút bên dưới để mở trang đăng nhập.</p>
            <a class="btn" href="${pageContext.request.contextPath}/login">Đăng nhập</a>
        </c:otherwise>
    </c:choose>

    <p class="hint">
        URL: <code>/</code> &rarr; index.jsp &nbsp;|&nbsp; <code>/home</code> &rarr; index.jsp (sau khi đăng nhập)
        &nbsp;|&nbsp; <code>/login</code> &nbsp;|&nbsp; <code>/error</code>
    </p>
</div>
</body>
</html>
