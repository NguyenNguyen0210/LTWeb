/* =====================================================================
   BT2 - CRUD Category voi JPA (Hibernate 7) - SQL Server
   Chay file nay 1 lan trong SQL Server Management Studio (SSMS) hoac sqlcmd
   ===================================================================== */

-- 1. Tao database (neu chua co)
IF DB_ID('webst2') IS NULL
BEGIN
    CREATE DATABASE webst2;
END
GO

USE webst2;
GO

-- 2. Bang categories
IF OBJECT_ID('dbo.categories', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.categories
    (
        CategoryId   INT IDENTITY (1,1) NOT NULL PRIMARY KEY,
        CategoryName NVARCHAR(50)  NOT NULL,
        Images       NVARCHAR(500) NULL,
        Status       INT           NOT NULL DEFAULT (1)
    );
END
GO

-- 3. Bang Videos
IF OBJECT_ID('dbo.Videos', 'U') IS NULL
BEGIN
    CREATE TABLE dbo.Videos
    (
        VideoId     VARCHAR(255)  NOT NULL PRIMARY KEY,
        Active      INT           NOT NULL DEFAULT (1),
        Description NVARCHAR(500) NULL,
        Poster      NVARCHAR(500) NULL,
        Title       NVARCHAR(500) NULL,
        [Views]     INT           NOT NULL DEFAULT (0),
        CategoryId  INT           NULL,
        CONSTRAINT FK_Videos_categories FOREIGN KEY (CategoryId)
            REFERENCES dbo.categories (CategoryId)
    );
END
GO

-- 4. Du lieu mau (7 danh muc -> du 2 trang voi PAGE_SIZE = 5)
IF NOT EXISTS (SELECT 1 FROM dbo.categories)
BEGIN
    INSERT INTO dbo.categories (CategoryName, Images, Status) VALUES
        (N'Điện thoại',      N'https://picsum.photos/seed/phone/400/300',  1),
        (N'Laptop',          N'https://picsum.photos/seed/laptop/400/300', 1),
        (N'Máy tính bảng',   N'https://picsum.photos/seed/tablet/400/300', 1),
        (N'Tai nghe',        N'https://picsum.photos/seed/audio/400/300',  1),
        (N'Đồng hồ',         N'https://picsum.photos/seed/watch/400/300',  0),
        (N'Phụ kiện',        N'https://picsum.photos/seed/acc/400/300',    1),
        (N'Camera',          N'https://picsum.photos/seed/camera/400/300', 0);
END
GO

-- 5. Du lieu mau cho Videos (de thu quan he 1-n Category - Video)
IF NOT EXISTS (SELECT 1 FROM dbo.Videos)
BEGIN
    INSERT INTO dbo.Videos (VideoId, Active, Description, Poster, Title, [Views], CategoryId)
    VALUES ('v01', 1, N'Video giới thiệu iPhone', NULL, N'Review iPhone', 1200,
            (SELECT TOP 1 CategoryId FROM dbo.categories WHERE CategoryName = N'Điện thoại')),
           ('v02', 1, N'Video giới thiệu Laptop', NULL, N'Review Laptop', 850,
            (SELECT TOP 1 CategoryId FROM dbo.categories WHERE CategoryName = N'Laptop'));
END
GO

SELECT * FROM dbo.categories;
SELECT * FROM dbo.Videos;
