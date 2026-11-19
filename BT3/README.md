# BT3 — SiteMesh 3, validation và hồ sơ

BT3 kế thừa đăng nhập, OTP, danh mục và video từ BT2. Ứng dụng dùng JDK 21, Maven, Tomcat 11 và SQL Server.

- SiteMesh 3.3.0 dùng `web` cho trang người dùng và `admin` cho phần quản trị. Cả hai dùng Bootstrap 5.3 và các phần JSP trong `common/`. Quy tắc trang nằm trong `WEB-INF/sitemesh3.xml`. Luồng API và tệp ảnh/video được loại khỏi bộ lọc trang trí.
- Máy chủ kiểm tra các form đăng nhập, đăng ký, quên mật khẩu, mã OTP và thêm/sửa danh mục, video. Các trường bắt buộc và giới hạn độ dài cũng được khai báo trên form.
- `/profile` cho phép sửa họ tên, số điện thoại và tải ảnh đại diện bằng multipart. Ảnh được gửi lên Cloudinary, rồi ứng dụng lưu URL HTTPS. Đổi email yêu cầu mật khẩu hiện tại và mã OTP gửi tới email mới. Email đăng nhập chỉ đổi sau khi xác nhận thành công. OTP có hiệu lực 5 phút, tối đa 5 lần thử trong phiên.

## Cài đặt

1. Bật SQL Server. Tạo cơ sở dữ liệu mẫu mới bằng [`database/init.sql`](database/init.sql). Script đặt dữ liệu trong `webst3` và tạo lại các bảng nếu đã tồn tại. Chỉ chạy script trên cơ sở dữ liệu mẫu cần khởi tạo lại.
2. Cấu hình các biến sau trong `bin/setenv.bat` của Tomcat hoặc trong môi trường chạy Tomcat.

   ```bat
   set "LTWEB_DB_USER=sa"
   set "LTWEB_DB_PASSWORD=your_sql_server_password"
   set "CLOUDINARY_URL=cloudinary://API_KEY:API_SECRET@CLOUD_NAME"
   ```

   URL JDBC mặc định là `jdbc:sqlserver://localhost:1433;encrypt=true;trustServerCertificate=true;databaseName=webst3`. Dùng `LTWEB_DB_URL` nếu địa chỉ SQL Server khác.

3. Để gửi OTP qua SMTP thật, thêm các thuộc tính sau vào `CATALINA_OPTS` trong `bin/setenv.bat`. Gmail dùng App Password. Khi chưa có cấu hình SMTP, OTP xuất hiện trong console Tomcat để chạy thử local.

   ```bat
   set "CATALINA_OPTS=%CATALINA_OPTS% -Dmail.username=example@gmail.com -Dmail.password=your_app_password -Dmail.from=example@gmail.com"
   ```

4. Chạy `mvn clean verify` trong `BT3` để tạo `target/bt3.war`. Chạy `run.bat` để build, triển khai lên Tomcat và mở [trang chủ BT3](http://localhost:8080/bt3/home).

Tài khoản mẫu `admin@ltweb.vn` và `user@ltweb.vn` có mật khẩu `123456`. Đây là tài khoản thử nghiệm trong dữ liệu mẫu.

## Kiểm tra nhanh

- Sau khi đăng nhập, mở **Hồ sơ** và thử lưu họ tên, số điện thoại. Tải ảnh JPEG, PNG hoặc GIF dưới 5 MB và kiểm tra ảnh khi mở lại trang.
- Nhập email mới và mật khẩu hiện tại. Trước khi xác nhận, đăng nhập vẫn dùng email cũ. Sau khi nhập đúng OTP, thử đăng nhập bằng email mới.
- Đăng nhập bằng tài khoản admin rồi mở `/admin/categories` để xem layout quản trị. Thử nhập dữ liệu thiếu hoặc vượt giới hạn trong form danh mục và video.

Tài liệu tham khảo gồm [SiteMesh 3](https://github.com/sitemesh/sitemesh3), [Bootstrap Album](https://getbootstrap.com/docs/5.3/examples/album/) và [Cloudinary Java SDK](https://cloudinary.com/documentation/java_integration).
