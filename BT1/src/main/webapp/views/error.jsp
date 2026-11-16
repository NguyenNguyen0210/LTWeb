<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập thất bại | LTWeb</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f4f6fb; color: #1f2937; padding: 24px;
        }
        .card {
            background: #fff; width: 100%; max-width: 460px; padding: 40px 36px; text-align: center;
            border-radius: 16px; box-shadow: 0 10px 30px rgba(15, 23, 42, .08);
            border-top: 6px solid #dc2626;
        }
        .icon {
            width: 64px; height: 64px; margin: 0 auto 18px; border-radius: 50%;
            background: #fef2f2; color: #dc2626; font-size: 34px; font-weight: 700;
            display: flex; align-items: center; justify-content: center;
        }
        h1 { margin: 0 0 10px; font-size: 24px; color: #991b1b; }
        p { color: #4b5563; line-height: 1.6; margin: 6px 0; }
        .btn {
            display: inline-block; margin-top: 24px; padding: 12px 26px; border-radius: 10px;
            background: #00539b; color: #fff; text-decoration: none; font-weight: 600; font-size: 15px;
        }
        .btn:hover { background: #003f75; }
        code { background: #eef2ff; color: #3730a3; padding: 2px 6px; border-radius: 4px; }
        .status { margin-top: 22px; font-size: 12px; color: #9ca3af; }
    </style>
</head>
<body>
<div class="card">
    <div class="icon">!</div>
    <h1>Đăng nhập thất bại</h1>
    <p>Tên đăng nhập hoặc mật khẩu không đúng.</p>
    <p>Vui lòng kiểm tra lại và thử lại.</p>

    <a class="btn" href="${pageContext.request.contextPath}/login">Thử lại</a>

    <div class="status">
        Đây là nội dung của <code>error.jsp</code> &middot; HTTP 401
    </div>
</div>
</body>
</html>
