<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Đăng nhập | LTWeb</title>
    <style>
        * { box-sizing: border-box; }
        body {
            margin: 0; min-height: 100vh; display: flex; align-items: center; justify-content: center;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f4f6fb; color: #1f2937; padding: 24px;
        }
        .card {
            background: #fff; width: 100%; max-width: 420px; padding: 40px 36px;
            border-radius: 16px; box-shadow: 0 10px 30px rgba(15, 23, 42, .08);
            border-top: 6px solid #00539b;
        }
        .brand { font-size: 13px; letter-spacing: .08em; text-transform: uppercase; color: #00539b; font-weight: 700; }
        h1 { margin: 10px 0 24px; font-size: 26px; }
        label { display: block; font-size: 14px; font-weight: 600; margin-bottom: 6px; color: #374151; }
        input[type="text"], input[type="password"] {
            width: 100%; padding: 12px 14px; font-size: 15px; border: 1px solid #d1d5db;
            border-radius: 10px; outline: none; margin-bottom: 18px; transition: border-color .2s, box-shadow .2s;
        }
        input:focus { border-color: #00539b; box-shadow: 0 0 0 3px rgba(0, 83, 155, .12); }
        button {
            width: 100%; padding: 13px; font-size: 16px; font-weight: 600; color: #fff;
            background: #00539b; border: 0; border-radius: 10px; cursor: pointer; transition: background .2s;
        }
        button:hover { background: #003f75; }
        .back { display: block; text-align: center; margin-top: 18px; font-size: 14px; color: #6b7280; text-decoration: none; }
        .back:hover { color: #00539b; }
        .note { margin-top: 22px; padding: 12px 14px; background: #f9fafb; border: 1px dashed #e5e7eb;
                border-radius: 10px; font-size: 13px; color: #6b7280; line-height: 1.6; }
        code { background: #eef2ff; color: #3730a3; padding: 2px 6px; border-radius: 4px; }
    </style>
</head>
<body>
<div class="card">
    <div class="brand">HCMUTE &middot; LTWeb</div>
    <h1>Đăng nhập</h1>

    <form action="${pageContext.request.contextPath}/login" method="post">
        <label for="username">Tên đăng nhập</label>
        <input type="text" id="username" name="username" placeholder="admin" required autofocus>

        <label for="password">Mật khẩu</label>
        <input type="password" id="password" name="password" placeholder="••••••" required>

        <button type="submit">Login</button>
    </form>

    <a class="back" href="${pageContext.request.contextPath}/">&larr; Về trang chủ</a>

    <div class="note">
        Tài khoản mẫu: <code>admin</code> / <code>123456</code> hoặc <code>student</code> / <code>123456</code>
    </div>
</div>
</body>
</html>
