/* =====================================================================
   BT3 - CRUD Category + Video bang JPA (Hibernate 7) - SQL Server
   Chay file nay 1 lan trong SQL Server Management Studio (SSMS) hoac sqlcmd

   CANH BAO: script XOA va tao lai 3 bang users / categories / Videos.
             Du lieu cu trong 3 bang nay se mat.
             (Can thiet vi bang Videos phai co them cot UserId.)
   ===================================================================== */

IF DB_ID('webst3') IS NULL
BEGIN
    CREATE DATABASE webst3;
END
GO

USE webst3;
GO

-- ---------- Xoa bang cu theo thu tu khoa ngoai ----------
IF OBJECT_ID('dbo.Videos', 'U') IS NOT NULL DROP TABLE dbo.Videos;
IF OBJECT_ID('dbo.categories', 'U') IS NOT NULL DROP TABLE dbo.categories;
IF OBJECT_ID('dbo.users', 'U') IS NOT NULL DROP TABLE dbo.users;
GO

-- =====================================================================
-- 1. users
-- =====================================================================
CREATE TABLE dbo.users
(
    UserId    INT IDENTITY (1,1) NOT NULL PRIMARY KEY,
    Email     NVARCHAR(100) NOT NULL,
    Fullname  NVARCHAR(100) NULL,
    Password  NVARCHAR(100) NOT NULL,   -- chuoi BCrypt: $2a$10$...
    Phone     NVARCHAR(20)  NULL,
    Avatar    NVARCHAR(500) NULL,
    Role      INT NOT NULL DEFAULT (0), -- 0: user, 1: admin
    Status    INT NOT NULL DEFAULT (0), -- 0: chua kich hoat, 1: hoat dong, -1: bi khoa
    Otp       NVARCHAR(10)  NULL,
    OtpExpiry DATETIME2     NULL,
    CreatedAt DATETIME2     NULL,
    CONSTRAINT UQ_users_Email UNIQUE (Email)
);
GO

-- =====================================================================
-- 2. categories
-- =====================================================================
CREATE TABLE dbo.categories
(
    CategoryId   INT IDENTITY (1,1) NOT NULL PRIMARY KEY,
    CategoryName NVARCHAR(50)  NOT NULL,
    Images       NVARCHAR(500) NULL,
    Status       INT           NOT NULL DEFAULT (1)
);
GO

-- =====================================================================
-- 3. Videos  (nhieu video thuoc 1 user  ->  quan he 1-n)
-- =====================================================================
CREATE TABLE dbo.Videos
(
    VideoId     VARCHAR(255)  NOT NULL PRIMARY KEY,
    Active      INT           NOT NULL DEFAULT (1),
    Description NVARCHAR(500) NULL,
    Poster      NVARCHAR(500) NULL,
    Title       NVARCHAR(500) NULL,
    [Views]     INT           NOT NULL DEFAULT (0),
    VideoUrl    NVARCHAR(500) NULL,
    CreatedAt   DATETIME2     NULL,
    CategoryId  INT           NULL,
    UserId      INT           NOT NULL,
    CONSTRAINT FK_Videos_categories FOREIGN KEY (CategoryId) REFERENCES dbo.categories (CategoryId),
    CONSTRAINT FK_Videos_users FOREIGN KEY (UserId) REFERENCES dbo.users (UserId)
);
GO

CREATE INDEX IX_Videos_UserId ON dbo.Videos (UserId);
CREATE INDEX IX_Videos_CreatedAt ON dbo.Videos (CreatedAt DESC);
GO

-- =====================================================================
-- 4. Du lieu mau
--    Mat khau cua ca 3 tai khoan duoi day deu la: 123456
--    (da duoc bam bang BCrypt, khong the dao nguoc)
-- =====================================================================
INSERT INTO dbo.users (Email, Fullname, Password, Role, Status, CreatedAt) VALUES
    ('admin@ltweb.vn', N'Quản trị viên',
     '$2a$10$nhi5XK0UCp0oN.h0TbyDk.h7R5tWPufX4fROPlydtRPFKvqU4U7dO', 1, 1, SYSDATETIME()),
    ('user@ltweb.vn', N'Nguyễn Văn A',
     '$2a$10$D7Vdh8DIRjA58VmnzB65.u/4.ySpjskyqi.EF5ICP1dXtLGqXleny', 0, 1, SYSDATETIME()),
    ('pending@ltweb.vn', N'Trần Thị B',
     '$2a$10$jxW/.QS2dhbJ.T/qSIR4re.vEI9QTR8EXwctDNu55dWtVodOtiaT.', 0, 0, SYSDATETIME());
GO

INSERT INTO dbo.categories (CategoryName, Images, Status) VALUES
    (N'Điện thoại',    N'https://picsum.photos/seed/phone/400/300',  1),
    (N'Laptop',        N'https://picsum.photos/seed/laptop/400/300', 1),
    (N'Máy tính bảng', N'https://picsum.photos/seed/tablet/400/300', 1),
    (N'Tai nghe',      N'https://picsum.photos/seed/audio/400/300',  1),
    (N'Đồng hồ',       N'https://picsum.photos/seed/watch/400/300',  0),
    (N'Phụ kiện',      N'https://picsum.photos/seed/acc/400/300',    1),
    (N'Camera',        N'https://picsum.photos/seed/camera/400/300', 0);
GO

-- 8 video cua user@ltweb.vn (du 2 trang voi 6 video/trang) + 2 video cua admin
DECLARE @uid INT = (SELECT UserId FROM dbo.users WHERE Email = 'user@ltweb.vn');
DECLARE @aid INT = (SELECT UserId FROM dbo.users WHERE Email = 'admin@ltweb.vn');
DECLARE @c1 INT = (SELECT CategoryId FROM dbo.categories WHERE CategoryName = N'Điện thoại');
DECLARE @c2 INT = (SELECT CategoryId FROM dbo.categories WHERE CategoryName = N'Laptop');

INSERT INTO dbo.Videos (VideoId, Active, Description, Poster, Title, [Views], VideoUrl, CreatedAt, CategoryId, UserId) VALUES
    ('v01', 1, N'Video mẫu Big Buck Bunny (mp4 trực tiếp)', N'https://picsum.photos/seed/v1/640/360',
     N'Big Buck Bunny - video mẫu 1', 1200,
     N'https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4',
     DATEADD(HOUR, -1, SYSDATETIME()), @c1, @uid),

    ('v02', 1, N'Video mẫu hoa (mp4 trực tiếp)', N'https://picsum.photos/seed/v2/640/360',
     N'Video hoa - video mẫu 2', 850,
     N'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
     DATEADD(HOUR, -2, SYSDATETIME()), @c2, @uid),

    ('v03', 1, N'Video nhúng từ YouTube', N'https://picsum.photos/seed/v3/640/360',
     N'Big Buck Bunny (YouTube) - video mẫu 3', 640,
     N'https://www.youtube.com/watch?v=aqz-KE-bpKQ',
     DATEADD(HOUR, -3, SYSDATETIME()), @c2, @uid),

    ('v04', 1, N'Video mẫu Big Buck Bunny 2', N'https://picsum.photos/seed/v4/640/360',
     N'Big Buck Bunny 360p - video mẫu 4', 410,
     N'https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4',
     DATEADD(HOUR, -4, SYSDATETIME()), @c1, @uid),

    ('v05', 1, N'Video mẫu hoa 2', N'https://picsum.photos/seed/v5/640/360',
     N'Video hoa - video mẫu 5', 320,
     N'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
     DATEADD(HOUR, -5, SYSDATETIME()), @c1, @uid),

    ('v06', 1, N'Video mẫu Big Buck Bunny 3', N'https://picsum.photos/seed/v6/640/360',
     N'Big Buck Bunny 360p - video mẫu 6', 210,
     N'https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4',
     DATEADD(HOUR, -6, SYSDATETIME()), @c2, @uid),

    ('v07', 1, N'Video mẫu hoa 3', N'https://picsum.photos/seed/v7/640/360',
     N'Video hoa - video mẫu 7', 180,
     N'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
     DATEADD(HOUR, -7, SYSDATETIME()), @c2, @uid),

    ('v08', 1, N'Video mẫu Big Buck Bunny 4', N'https://picsum.photos/seed/v8/640/360',
     N'Big Buck Bunny 360p - video mẫu 8', 95,
     N'https://test-videos.co.uk/vids/bigbuckbunny/mp4/h264/360/Big_Buck_Bunny_360_10s_1MB.mp4',
     DATEADD(HOUR, -8, SYSDATETIME()), @c1, @uid),

    ('v09', 1, N'Video cua admin', N'https://picsum.photos/seed/v9/640/360',
     N'Video của quản trị viên', 45,
     N'https://www.youtube.com/watch?v=aqz-KE-bpKQ',
     DATEADD(HOUR, -9, SYSDATETIME()), @c1, @aid),

    ('v10', 1, N'Video cua admin 2', N'https://picsum.photos/seed/v10/640/360',
     N'Video thứ hai của quản trị viên', 30,
     N'https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4',
     DATEADD(HOUR, -10, SYSDATETIME()), @c2, @aid);
GO

SELECT u.UserId, u.Email, u.Role, u.Status, COUNT(v.VideoId) AS SoVideo
FROM dbo.users u LEFT JOIN dbo.Videos v ON v.UserId = u.UserId
GROUP BY u.UserId, u.Email, u.Role, u.Status;
GO
