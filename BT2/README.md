# BT2 — JSP/Servlet + JPA: tài khoản, OTP email, CRUD Category & Video

Bài tập 02 (mở rộng). Kiến trúc 3 tầng `Controller → Service → DAO → JPA`,
chạy trên **Tomcat 11 + SQL Server + Hibernate 7**.

## Chức năng

| Nhóm | Chức năng |
|---|---|
| Tài khoản | Đăng ký + **kích hoạt bằng OTP gửi qua email**, đăng nhập mật khẩu **hash BCrypt**, đăng xuất |
| Quên mật khẩu | Gửi **OTP qua email**, đặt lại mật khẩu mới |
| Category | CRUD (chỉ admin), tìm kiếm, phân trang, upload ảnh |
| Video | CRUD video **của user đang đăng nhập**, upload file mp4/poster bằng Multipart |
| Trang chủ | `/home` — **10 video mới nhất** |
| Danh sách | `/video` — video của user, **6 video/trang** |
| Xem video | `/video/detail?id=...` — chi tiết + **play** (mp4 upload, link mp4, hoặc YouTube) |

Quan hệ: `User (1) — (n) Video`, `Category (1) — (n) Video`.

---

## 1. Môi trường

| Thành phần | Phiên bản / Đường dẫn |
|---|---|
| JDK | 21.0.10 (`D:\Java`) — project compile với `release 17` |
| Maven | 3.9.14 |
| Tomcat | 11.0.27 (`D:\apache-tomcat-11.0.27`) |
| SQL Server | `localhost:1433`, database `webst2` |
| Hibernate ORM | 7.4.6.Final |
| Jakarta Mail | 2.1.5 + Angus Mail 2.0.5 |
| BCrypt | jBCrypt 0.4 |

> Tomcat 11 = Jakarta EE 11 → mọi import là `jakarta.*`, **không** dùng `javax.*`.
> JSTL 3.0 dùng namespace `jakarta.tags.core` / `jakarta.tags.functions`.

---

## 2. Chuẩn bị cơ sở dữ liệu

1. Bật SQL Server (Services → `SQL Server (MSSQLSERVER)` → Start), bật TCP/IP.
2. Mở SSMS, chạy [`database/init.sql`](database/init.sql).
   Script này **xoá và tạo lại** 3 bảng `users`, `categories`, `Videos` + dữ liệu mẫu.
3. Kiểm tra tài khoản DB trong
   [`src/main/resources/META-INF/persistence.xml`](src/main/resources/META-INF/persistence.xml)
   (`sa` / `1234567@a$`) — sửa nếu máy bạn dùng mật khẩu khác.

### Tài khoản mẫu (mật khẩu đều là `123456`)

| Email | Vai trò | Trạng thái | Ghi chú |
|---|---|---|---|
| `admin@ltweb.vn` | admin | đã kích hoạt | vào được `/admin/categories` |
| `user@ltweb.vn` | user | đã kích hoạt | có 8 video mẫu → `/video` đủ 2 trang |
| `pending@ltweb.vn` | user | **chưa kích hoạt** | đăng nhập sẽ bị chặn |

---

## 3. Cấu hình gửi email OTP

Mở [`src/main/resources/mail.properties`](src/main/resources/mail.properties):

```properties
mail.smtp.host=smtp.gmail.com
mail.smtp.port=587
mail.username=your_email@gmail.com
mail.password=xxxx xxxx xxxx xxxx   # App Password 16 ký tự của Gmail
mail.from=your_email@gmail.com
```

- Gmail: bật **2-Step Verification** → tạo **App passwords** tại
  <https://myaccount.google.com/apppasswords> → dán 16 ký tự vào `mail.password`.
  **Không** dùng mật khẩu đăng nhập Gmail.
- **Không commit mật khẩu thật lên GitHub.** Có thể đặt ngoài file bằng system property
  trong `D:\apache-tomcat-11.0.27\bin\setenv.bat`:

  ```bat
  set "CATALINA_OPTS=-Dmail.username=your_email@gmail.com -Dmail.password=xxxxxxxxxxxxxxxx"
  ```

- **Chưa cấu hình SMTP?** Hệ thống tự chuyển sang **DEV mode**: không gửi mail mà in OTP ra
  console Tomcat (`catalina.out` / cửa sổ chạy Tomcat). Trang `/verify` và `/reset` có
  banner vàng nhắc điều này. Xem OTP ở đó để test.

---

## 4. Chạy project

Double-click **`run.bat`** → build, copy `bt2.war` vào Tomcat, mở trình duyệt.
Tắt Tomcat: **`stop.bat`**.

Chạy tay:

```bash
mvn clean package
cp target/bt2.war /d/apache-tomcat-11.0.27/webapps/
/d/apache-tomcat-11.0.27/bin/startup.bat
```

---

## 5. Các URL

| URL | Chức năng | Quyền |
|---|---|---|
| `/bt2/` | chuyển về `/home` | công khai |
| `/bt2/home` | 10 video mới nhất | công khai |
| `/bt2/register` | form đăng ký | công khai |
| `/bt2/verify?email=...` | nhập OTP kích hoạt | công khai |
| `/bt2/login` · `/bt2/logout` | đăng nhập / đăng xuất | công khai |
| `/bt2/forgot` → `/bt2/reset?email=...` | quên mật khẩu 2 bước | công khai |
| `/bt2/video` | video của tôi, 6/trang (`?page=2`, `?keyword=`) | đăng nhập |
| `/bt2/video/add` · `/video/edit?id=` · `/video/delete?id=` | CRUD video | chủ sở hữu |
| `/bt2/video/detail?id=` | xem + play | công khai |
| `/bt2/admin/categories` (+ add/edit/delete) | CRUD Category | **admin** |
| `/bt2/image?fname=` · `/bt2/media?fname=` | phục vụ ảnh / video đã upload (hỗ trợ HTTP Range) | công khai |

File upload lưu ở `C:\Users\<user>\ltweb-uploads` (đổi bằng `-Dltweb.upload.dir=...`),
**ngoài** thư mục webapps để không mất khi deploy lại.

---

## 6. Cấu trúc

```
BT2/src/main/
├── java/vn/edu/hcmute/
│   ├── entity/      User, Category, Video          (@Entity, @NamedQuery, quan hệ 1-n)
│   ├── config/      JpaConfig, AppContextListener, TestJpa
│   ├── dao/         IUserDao/UserDao, ICategoryDao/CategoryDao, IVideoDao/VideoDao
│   ├── service/     UserServiceImpl, CategoryServiceImpl, VideoServiceImpl, MailService
│   ├── controller/  HomeServlet, VideoServlet, CategoryController, ImageServlet, IndexServlet
│   │   └── auth/    Login, Logout, Register, Verify, Forgot, Reset
│   ├── filter/      AuthFilter        (chặn /admin/* và /video*)
│   └── util/        PasswordUtil, OtpUtil, UploadUtil, Flash, Constants
├── resources/       META-INF/persistence.xml, mail.properties
└── webapp/
    ├── css/style.css
    ├── index.jsp
    ├── WEB-INF/web.xml
    └── views/
        ├── inc/      head.jspf, foot.jspf      (layout dùng chung)
        ├── auth/     login, register, verify, forgot, reset
        ├── video/    list, add, edit, detail
        ├── admin/    category-list, category-add, category-edit
        ├── home.jsp
        └── error.jsp
```

---

## 7. Kiến thức trọng tâm (dễ bị hỏi khi bảo vệ)

**Hash mật khẩu bằng BCrypt** — `PasswordUtil.hash()` sinh chuỗi `$2a$10$...`,
DB **không bao giờ** lưu mật khẩu gốc. BCrypt có salt ngẫu nhiên nhúng trong hash
(2 người cùng mật khẩu → 2 hash khác nhau) và có cost 10 (~2¹⁰ vòng lặp) nên rất
chậm khi brute-force. Kiểm tra bằng `BCrypt.checkpw()`, không thể "giải mã".

**OTP** — 6 chữ số sinh bằng `SecureRandom` (không dùng `Random` vì có thể đoán trạng thái
tiếp theo), lưu vào `users.Otp` + `users.OtpExpiry`, hết hạn sau **5 phút**, dùng xong xoá.

**Kích hoạt tài khoản** — đăng ký tạo user với `Status = 0`. Chỉ khi nhập đúng OTP mới
đổi `Status = 1`. `login()` chặn tài khoản `Status = 0` (chưa kích hoạt) và `-1` (bị khoá).

**Session fixation** — sau khi đăng nhập thành công, `LoginServlet` **huỷ session cũ** rồi
tạo session mới trước khi gắn `user` vào. Nếu dùng lại session cũ, kẻ tấn công đã biết
JSESSIONID trước đó sẽ chiếm được phiên.

**AuthFilter** — `@WebFilter("/admin/*", "/video", "/video/*")`: chưa đăng nhập thì lưu
trang đang muốn vào rồi redirect `/login`; không phải admin thì chặn `/admin/*`.

**Phân quyền dữ liệu** — mỗi video có `user_id`; sửa/xoá video phải kiểm tra
`video.user.userid == session.user.userid` (xem `VideoServlet.canEdit`), không chỉ ẩn nút trên UI.

**Quan hệ 1-n** — `User` có `@OneToMany(mappedBy = "user")`, `Video` có
`@ManyToOne @JoinColumn(name = "UserId", nullable = false)`. `mappedBy` = "khoá ngoại do
phía Video quản lý"; thiếu nó Hibernate tự sinh thêm bảng trung gian.

**Upload Multipart** — form `enctype="multipart/form-data"` + servlet `@MultipartConfig`;
`Part.write(path)` lưu file, `getSubmittedFileName()` lấy tên gốc. Tên file được đổi thành
`timestamp+random.ext` để không ghi đè. Chỉ nhận đúng phần mở rộng cho phép.

**Phục vụ file ngoài webapp** — file upload nằm ngoài WAR nên không truy cập trực tiếp được;
`/media?fname=` đọc file và trả về, có kiểm tra `..`/`/` để chặn path traversal, và hỗ trợ
header `Range` (HTTP 206) để video **tua được**.

**`hbm2ddl.auto=update`** — Hibernate tự tạo/cập nhật bảng theo entity, không xoá dữ liệu.

---

## 8. Những chỗ khác với hướng dẫn (và lý do)

| Trong hướng dẫn | Trong bài này | Lý do |
|---|---|---|
| `JpaConfig` tạo `EntityManagerFactory` mỗi lần gọi | singleton, tạo 1 lần | EMF rất nặng; tạo mỗi request sẽ rò rỉ connection |
| `searchByName`: `WHERE c.catename` | `c.categoryname` | sai tên field → lỗi JPQL |
| `getSingleResult()` rồi so `== null` | `getResultList()`, rỗng trả `null` | `getSingleResult` ném `NoResultException`, không trả null |
| `@Data` trên 2 entity có quan hệ 2 chiều | thêm `@ToString.Exclude`, `@EqualsAndHashCode.Exclude` | tránh `StackOverflowError` do toString đệ quy |
| JSP `images.substring(0,5)` | `fn:startsWith` | null hoặc chuỗi < 5 ký tự → lỗi EL |
| Thiếu servlet `/image` | có `ImageServlet` (`/image`, `/media`) | JSP trỏ `/image?fname=` nhưng hướng dẫn không viết servlet |
| Chỉ CRUD Category | thêm tầng User + Auth + Video | theo yêu cầu bài tập mới |

---

## 9. Lỗi thường gặp

| Lỗi | Cách sửa |
|---|---|
| `The TCP/IP connection to the host localhost, port 1433 has failed` | SQL Server chưa chạy / chưa bật TCP/IP |
| `Cannot open database "webst2"` | Chưa chạy `database/init.sql` |
| `Login failed for user 'sa'` | Sai mật khẩu trong `persistence.xml`, hoặc `sa` bị disable |
| Đăng ký xong không nhận được mail | Chưa điền `mail.properties` → xem OTP ở console Tomcat; hoặc Gmail chặn (phải dùng App Password) |
| `Authentication failed` khi gửi mail | Dùng mật khẩu Gmail thường thay vì App Password |
| Tiếng Việt trong `.jspf` bị `Trang chá»§` | `web.xml` đã khai báo `<jsp-property-group>` `*.jspf` → `UTF-8`; nếu tự thêm file `.jspf` mới thì kiểm tra lại khối này |
| Video không play | Link YouTube phải là video công khai; file upload phải là `mp4/webm/ogg` mà trình duyệt hỗ trợ |
| Ảnh/video không hiện | Kiểm tra thư mục `ltweb-uploads` và quyền đọc của Tomcat |
