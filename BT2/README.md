# BT2 — CRUD Category bằng JPA (Hibernate 7 + Jakarta EE 11)

Bài tập CRUD đầy đủ 3 tầng (`Controller → Service → DAO → JPA`) cho entity `Category`,
quan hệ 1-n với `Video`, upload ảnh, tìm kiếm và phân trang. Chạy trên Tomcat 11 + SQL Server.

---

## 1. Môi trường

| Thành phần | Phiên bản / Đường dẫn |
|---|---|
| JDK | 21.0.10 (`D:\Java`) — project compile với `release 17` |
| Maven | 3.9.14 |
| Tomcat | 11.0.27 (`D:\apache-tomcat-11.0.27`) |
| SQL Server | `localhost:1433`, database `webst2` |
| Hibernate ORM | 7.4.6.Final |
| Jakarta Persistence API | 3.2.0 |

> Tomcat 11 = Jakarta EE 11 → mọi import là `jakarta.*`, **không** dùng `javax.*`.
> JSTL 3.0 dùng namespace `jakarta.tags.core` / `jakarta.tags.functions`.

---

## 2. Chuẩn bị cơ sở dữ liệu

**Bước 1.** Bật SQL Server (Services → `SQL Server (MSSQLSERVER)` → Start) và bật
TCP/IP trong SQL Server Configuration Manager (nếu chưa).

**Bước 2.** Mở SSMS, chạy file [`database/init.sql`](database/init.sql). File này:

- tạo database `webst2` nếu chưa có;
- tạo 2 bảng `categories` và `Videos` (kèm khóa ngoại);
- thêm 7 danh mục + 2 video mẫu.

**Bước 3.** Kiểm tra tài khoản đăng nhập trong
[`src/main/resources/META-INF/persistence.xml`](src/main/resources/META-INF/persistence.xml):

```xml
<property name="jakarta.persistence.jdbc.user" value="sa"/>
<property name="jakarta.persistence.jdbc.password" value="1234567@a$"/>
```

Nếu SQL Server của bạn dùng mật khẩu khác → **sửa đúng 2 dòng này**, không sửa chỗ nào khác.

---

## 3. Chạy project

Double-click **`run.bat`** → script tự build, copy `bt2.war` vào Tomcat, khởi động Tomcat và mở trình duyệt.

Hoặc chạy tay:

```bash
mvn clean package
cp target/bt2.war /d/apache-tomcat-11.0.27/webapps/
/d/apache-tomcat-11.0.27/bin/startup.bat
```

Tắt Tomcat: **`stop.bat`**.

---

## 4. Các URL

| URL | Chức năng |
|---|---|
| `http://localhost:8080/bt2/` | Tự chuyển sang trang danh sách |
| `/bt2/admin/categories` | Danh sách + phân trang + tìm kiếm (`?page=2`, `?keyword=laptop`) |
| `/bt2/admin/category/add` | Form thêm mới |
| `/bt2/admin/category/insert` | (POST) Thêm vào DB |
| `/bt2/admin/category/edit?id=1` | Form sửa |
| `/bt2/admin/category/update` | (POST) Cập nhật |
| `/bt2/admin/category/delete?id=1` | Xóa |
| `/bt2/image?fname=xxx.png` | Trả về file ảnh đã upload |

Ảnh upload được lưu ở `C:\Users\<user>\ltweb-uploads` (đổi được bằng `-Dltweb.upload.dir=...`).
Lưu **ngoài** thư mục webapps để không bị mất khi deploy lại.

---

## 5. Cấu trúc

```
BT2/
├── pom.xml
├── run.bat / stop.bat
├── database/init.sql
└── src/main/
    ├── java/vn/edu/hcmute/
    │   ├── entity/      Category.java, Video.java        (JPA entity + @NamedQuery)
    │   ├── config/      JpaConfig.java                    (EntityManagerFactory)
    │   │                AppContextListener.java           (đóng EMF khi shutdown)
    │   │                TestJpa.java                      (bước 5: test không cần web)
    │   ├── dao/         ICategoryDao.java, CategoryDao.java
    │   ├── service/     ICategoryService.java, CategoryServiceImpl.java
    │   ├── controller/  CategoryController.java, ImageServlet.java, IndexServlet.java
    │   └── util/        Constants.java
    ├── resources/META-INF/persistence.xml
    └── webapp/
        ├── css/style.css
        ├── index.jsp
        ├── WEB-INF/web.xml
        └── views/
            ├── admin/category-list.jsp, category-add.jsp, category-edit.jsp
            └── error.jsp
```

Luồng gọi: **Servlet → Service → DAO → EntityManager**. Controller không gọi thẳng DAO.

---

## 6. Kiến thức trọng tâm khi bảo vệ bài

**`@NamedQuery`** — câu JPQL đặt tên sẵn ở đầu entity, gọi bằng
`enma.createNamedQuery("Category.findAll", Category.class)`. Ưu điểm: JPQL được kiểm tra
lúc khởi động, không phải viết lại chuỗi mỗi lần dùng.

**`@GeneratedValue(strategy = IDENTITY)`** — khóa chính do cột `IDENTITY` của SQL Server sinh ra,
Hibernate không cần bảng sequence riêng.

**Quan hệ 1-n hai chiều** — `Category` có `@OneToMany(mappedBy = "category")`,
`Video` có `@ManyToOne @JoinColumn(name = "CategoryId")`. `mappedBy` nghĩa là
"khóa ngoại do phía `Video` quản lý". Thiếu `mappedBy` Hibernate sẽ tự tạo **thêm một bảng trung gian** — sai yêu cầu.

**Transaction** — mọi thao tác ghi đều `begin() → persist/merge/remove → commit()`,
có `rollback()` trong `catch` và `close()` trong `finally`.

**Multipart** — form phải có `enctype="multipart/form-data"` và servlet phải có
`@MultipartConfig`, nếu không `req.getPart(...)` sẽ trả `null`.

**JSTL** — `<%@ taglib prefix="c" uri="jakarta.tags.core" %>`. Dùng
`http://java.sun.com/jsp/jstl/core` sẽ ra lỗi `absolute uri cannot be resolved`.

---

## 7. Những chỗ khác với hướng dẫn (và lý do)

Mình giữ nguyên kiến trúc và tên phương thức của hướng dẫn, chỉ sửa các lỗi sẽ làm bài **không chạy được**:

| Trong hướng dẫn | Trong bài này | Lý do |
|---|---|---|
| `JpaConfig` tạo `EntityManagerFactory` mỗi lần gọi | Singleton, tạo 1 lần | EMF rất nặng; tạo mỗi request sẽ rò rỉ connection, vài chục request là SQL Server báo `too many connections` |
| `searchByName`: `WHERE c.catename LIKE ...` | `WHERE c.categoryname LIKE ...` | `catename` không phải tên field → `IllegalArgumentException` |
| `findByCategoryname` dùng `getSingleResult()` rồi kiểm tra `== null` | dùng `getResultList()`, rỗng thì trả `null` | `getSingleResult()` ném `NoResultException` khi không tìm thấy, không bao giờ trả `null` |
| `@Data` trên cả 2 entity có quan hệ 2 chiều | thêm `@ToString.Exclude`, `@EqualsAndHashCode.Exclude` | `toString()`/`hashCode()` sinh ra sẽ gọi vòng Category ↔ Video → `StackOverflowError` |
| Insert trùng tên thì im lặng bỏ qua | báo lỗi "Tên danh mục đã tồn tại" | Người dùng không biết tại sao bấm Insert mà không có gì xảy ra |
| Không kiểm tra `images` rỗng | có fallback `avatar.png` + kiểm tra định dạng file | Tránh `NullPointerException` và chặn upload file không phải ảnh |
| JSP gọi `cate.images.substring(0,5)` | dùng `fn:startsWith` | Nếu `images` null hoặc ngắn hơn 5 ký tự → lỗi EL |
| Thiếu servlet `/image` | có `ImageServlet` | JSP trỏ tới `/image?fname=...` nhưng hướng dẫn không viết servlet này |

Phần **thêm** so với yêu cầu: phân trang (dùng `findAll(page, size)` + `count()`),
tìm kiếm theo tên, thông báo flash, trang lỗi thân thiện, chặn path traversal ở `ImageServlet`.

---

## 8. Lỗi thường gặp

| Lỗi | Nguyên nhân / Cách sửa |
|---|---|
| `The TCP/IP connection to the host localhost, port 1433 has failed` | SQL Server chưa chạy, hoặc chưa bật TCP/IP |
| `Cannot open database "webst2"` | Chưa chạy `database/init.sql` |
| `Login failed for user 'sa'` | Sai mật khẩu trong `persistence.xml`, hoặc `sa` bị disable |
| `ClassNotFoundException: com.microsoft.sqlserver.jdbc.SQLServerDriver` | Thiếu dependency `mssql-jdbc` trong `pom.xml` |
| `Unknown persistence unit` | Tên unit trong `JpaConfig` khác tên trong `persistence.xml` |
| `No Persistence provider for EntityManager` | Thiếu `hibernate-core`, hoặc `persistence.xml` sai vị trí (phải ở `META-INF/`) |
| Ảnh không hiện | Kiểm tra thư mục `ltweb-uploads` có file chưa, và Tomcat có quyền đọc |
| Tiếng Việt bị `?` | `web.xml` đã set UTF-8; nếu vẫn lỗi thì kiểm tra collation của cột là `nvarchar` |
