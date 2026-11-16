# LTWeb — Bài tập Lập trình Web (JSP/Servlet/JPA)

Repo gồm các bài tập môn Lập trình Web, mỗi bài nằm trong một thư mục riêng.

| Bài | Nội dung | Công nghệ |
|---|---|---|
| [BT1](BT1/) | 3 tầng, 3 trang JSP, servlet xử lý 4 URL | Jakarta Servlet 6.1, JSP 4.0, JSTL 3.0, Tomcat 11 |
| [BT2](BT2/) | CRUD `Category` bằng JPA, quan hệ 1-n với `Video`, upload ảnh, tìm kiếm, phân trang | JPA 3.2, Hibernate 7.4.6, SQL Server, Tomcat 11 |

## Yêu cầu chung

- JDK 21 (`D:\Java`)
- Maven 3.9+
- Tomcat 11.0.27 (`D:\apache-tomcat-11.0.27`)
- SQL Server (chỉ BT2 cần)

Mỗi thư mục có `README.md` riêng hướng dẫn cài đặt và chạy chi tiết.

## Chạy nhanh

```bash
# BT1
cd BT1 && ./run.bat

# BT2 (chạy database/init.sql trước)
cd BT2 && ./run.bat
```

> `run.bat` build bằng Maven, copy file `.war` vào Tomcat rồi mở trình duyệt.
> Nếu Tomcat cài ở đường dẫn khác, sửa biến `CATALINA_HOME` ở đầu file `.bat`.

## Cấu trúc

```
LTWeb/
├── BT1/    Jakarta JSP/Servlet cơ bản (3 tầng)
└── BT2/    CRUD Category với JPA + Hibernate
```
