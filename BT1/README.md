# BT1 - Bài tập JSP/Servlet (Jakarta EE 11)

Project Maven hoàn chỉnh cho bài tập 45 phút: 3 tầng, 3 trang JSP, servlet xử lý 4 URL, chạy trên Tomcat 11.

Toàn bộ mã nguồn nằm trong thư mục `LTWeb\BT1`. Mở VS Code tại thư mục **BT1** (không phải LTWeb) để extension Java nhận đúng project.

## 1. Môi trường đã cài trên máy

| Thành phần | Đường dẫn |
|---|---|
| JDK 21.0.10 | `D:\Java` (JAVA_HOME đã set) |
| Maven 3.9.14 | `D:\apache-maven-3.9.14-bin\apache-maven-3.9.14` |
| Tomcat 11.0.27 | `D:\apache-tomcat-11.0.27` |
| VS Code + Java Pack + Community Server Connectors | đã cài |

## 2. Chạy project

Double-click **`LTWeb\BT1\run.bat`**. Script sẽ tự: build → copy `ltweb.war` vào Tomcat → khởi động Tomcat → mở trình duyệt.

Tắt Tomcat: double-click **`stop.bat`**.

Hoặc chạy tay bằng terminal:

```bash
mvn clean package
cp target/ltweb.war /d/apache-tomcat-11.0.27/webapps/
/d/apache-tomcat-11.0.27/bin/startup.bat
```

## 3. Các URL

| URL | Servlet | Hiển thị |
|---|---|---|
| `http://localhost:8080/ltweb/` | `IndexServlet` | `views/index.jsp` - trang chủ |
| `http://localhost:8080/ltweb/login` | `LoginServlet` | `views/login.jsp` - form đăng nhập |
| `http://localhost:8080/ltweb/home` | `HomeServlet` | `index.jsp` + lời chào (chỉ khi đã đăng nhập) |
| `http://localhost:8080/ltweb/error` | `ErrorServlet` | `views/error.jsp` - HTTP 401 |

Tài khoản mẫu: `admin / 123456` hoặc `student / 123456`.

Luồng: form login POST về `/login` → đúng thì `sendRedirect` sang `/home`, sai thì `sendRedirect` sang `/error`.

## 4. Cấu trúc 3 tầng

```
src/main/java/vn/edu/hcmute/
├── controller/     IndexServlet, LoginServlet, HomeServlet, ErrorServlet
├── service/        AuthService      <- nghiệp vụ đăng nhập
├── dao/            UserDAO          <- truy cập dữ liệu
└── model/          User             <- entity
src/main/webapp/
├── views/          index.jsp, login.jsp, error.jsp
└── WEB-INF/web.xml (Servlet 6.1)
```

Luồng gọi: `Servlet` → `AuthService` → `UserDAO`. Controller không được gọi thẳng DAO.

## 5. Lưu ý khi bảo vệ bài

- **JSTL 3.0 đổi namespace**: phải là `uri="jakarta.tags.core"`, không phải `http://java.sun.com/jsp/jstl/core`. Đây là lỗi hay bị trừ điểm nhất.
- **Không dùng `javax.servlet`**. Tomcat 11 là Jakarta EE 11, mọi import phải là `jakarta.servlet.*`.
- `jakarta.servlet-api` và `jakarta.servlet.jsp-api` để `scope=provided` (Tomcat đã có sẵn). Riêng **JSTL phải đóng gói vào WAR** vì Tomcat không kèm.
- Context path là `/ltweb` nên URL thật là `/ltweb/home`. Nếu thầy yêu cầu đúng `/home` thì đổi `<finalName>` trong `pom.xml` thành `ROOT` rồi deploy lại (phải xoá thư mục `webapps\ROOT` cũ trước).
- `web.xml` khai báo `version="6.1"`. Nếu dùng archetype cũ sinh ra 2.3 thì annotation `@WebServlet` sẽ không được quét.
- Mật khẩu đang lưu plaintext trong `UserDAO` - chỉ để demo, không dùng cho bài nộp thật có yêu cầu bảo mật.
