# LTWeb — Bài tập Lập trình Web (JSP/Servlet/JPA)

Repo gồm các bài tập môn Lập trình Web, mỗi bài nằm trong một thư mục riêng.

| Bài | Nội dung | Công nghệ |
|---|---|---|
| [BT1](BT1/) | 3 tầng, 3 trang JSP, servlet xử lý 4 URL | Jakarta Servlet 6.1, JSP 4.0, JSTL 3.0, Tomcat 11 |
| [BT2](BT2/) | Đăng ký + kích hoạt OTP qua email, đăng nhập mật khẩu hash BCrypt, đăng xuất, quên mật khẩu bằng OTP; CRUD `Category`; CRUD `Video` (quan hệ 1-n với `User`), trang chủ 10 video mới, `/video` phân trang 6/trang, trang xem và play video | JPA 3.2, Hibernate 7.4.6, Jakarta Mail, BCrypt, SQL Server, Tomcat 11 |
| [BT3](BT3/) | Layout SiteMesh 3 + Bootstrap, validation form, hồ sơ cá nhân, xác nhận email mới bằng OTP và tải ảnh đại diện lên Cloudinary | SiteMesh 3.3.0, Bootstrap 5.3, Cloudinary Java SDK |

### Tài khoản mẫu của BT2 (mật khẩu `123456`)

| Email | Vai trò |
|---|---|
| `admin@ltweb.vn` | admin — vào được `/admin/categories` |
| `user@ltweb.vn` | user — có 8 video mẫu |
| `pending@ltweb.vn` | user chưa kích hoạt (đăng nhập sẽ bị chặn) |

## Yêu cầu chung

- JDK 21 (`D:\Java`)
- Maven 3.9+
- Tomcat 11.0.27 (`D:\apache-tomcat-11.0.27`)
- SQL Server (BT2 và BT3)

Mỗi thư mục có `README.md` riêng hướng dẫn cài đặt và chạy chi tiết.

## Chạy nhanh

```bash
# BT1
cd BT1 && ./run.bat

# BT2 (chạy database/init.sql trước)
cd BT2 && ./run.bat

# BT3 (xem cấu hình trong BT3/README.md)
cd BT3 && ./run.bat
```

> `run.bat` build bằng Maven, copy file `.war` vào Tomcat rồi mở trình duyệt.
> Nếu Tomcat cài ở đường dẫn khác, sửa biến `CATALINA_HOME` ở đầu file `.bat`.

## Cấu trúc

```
LTWeb/
├── BT1/    Jakarta JSP/Servlet cơ bản (3 tầng)
├── BT2/    CRUD Category và Video với JPA + Hibernate
└── BT3/    SiteMesh, Bootstrap, validation và hồ sơ
```
