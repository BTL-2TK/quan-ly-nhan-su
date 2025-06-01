-- =========================
-- BẢNG TÀI KHOẢN NGƯỜI DÙNG
-- =========================
IF OBJECT_ID('TaiKhoanNguoiDung', 'U') IS NOT NULL DROP TABLE TaiKhoanNguoiDung;
CREATE TABLE TaiKhoanNguoiDung (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap NVARCHAR(50) NOT NULL UNIQUE,
    MatKhauHash NVARCHAR(256) NOT NULL,
    EmailXacThuc NVARCHAR(100),
    MaVaiTro INT,
    TrangThaiHoatDong BIT DEFAULT 1
);

-- =========================
-- BẢNG VAI TRÒ
-- =========================
IF OBJECT_ID('VaiTro', 'U') IS NOT NULL DROP TABLE VaiTro;
CREATE TABLE VaiTro (
    MaVaiTro INT IDENTITY(1,1) PRIMARY KEY,
    TenVaiTro NVARCHAR(50) NOT NULL
);

-- =========================
-- BẢNG ỨNG VIÊN (TUYỂN DỤNG)
-- =========================
CREATE TABLE UngVien (
    MaTuyenDung NVARCHAR(50) NOT NULL,
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai NVARCHAR(20),
    Email NVARCHAR(100),
    ChucVu NVARCHAR(50),
    TrinhDo NVARCHAR(50),
    MucLuongDeal NVARCHAR(50),
    TrangThai NVARCHAR(20) DEFAULT N'Chưa tuyển'
);

-- =========================
-- BẢNG NHÂN VIÊN (EMPLOYEES)
-- =========================
CREATE TABLE Employees (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    EmployeeCode NVARCHAR(50) NOT NULL,
    FirstName NVARCHAR(50) NOT NULL,
    LastName NVARCHAR(50) NOT NULL,
    Email NVARCHAR(100),
    Phone NVARCHAR(20),
    Address NVARCHAR(200),
    BirthDate DATE,
    Gender NVARCHAR(10),
    IdNumber NVARCHAR(20),
    DepartmentId INT,
    PositionId INT,
    HireDate DATE,
    Status NVARCHAR(20),
    AvatarPath NVARCHAR(200)
);

-- =========================
-- DỮ LIỆU MẪU VAI TRÒ
-- =========================
INSERT INTO VaiTro (TenVaiTro) VALUES (N'ADMIN'), (N'USER'), (N'HR_MANAGER');

-- =========================
-- DỮ LIỆU MẪU ỨNG VIÊN
-- =========================
INSERT INTO UngVien (MaTuyenDung, HoTen, SoDienThoai, Email, ChucVu, TrinhDo, MucLuongDeal, TrangThai) VALUES
(N'BCTD002', N'001 - Huỳnh Thanh H', N'0123123123', N'thanhhh@gmail.com', N'Phó phòng', N'12/12', N'14.000.000', N'Chưa tuyển'),
(N'BCTD001', N'002 - Trương Hoàng T', N'0299123123', N'truonghoangthai@gmail.com', N'Kĩ thuật viên', N'12/12', N'11.500.000', N'Đã tuyển'),
(N'BCTD002', N'003 - Lý Thành T', N'0123123132', N'thanhtu@gmail.com', N'Phó phòng', N'12/12', N'17.000.000', N'Chưa tuyển'),
(N'BCTD003', N'007 - Đỗ Thị Như N', N'0123123132', N'nhun12@gmail.com', N'Quản lý nhân sự', N'Không', N'15.000.000', N'Đã tuyển'),
(N'BCTD001', N'012 - Trần Thị C', N'0123123132', N'thic123@gmail.com', N'Chuyên viên SEO', N'Không', N'12.000.000', N'Chưa tuyển'),
(N'BCTD002', N'020 - Nguyễn Văn P', N'0123123132', N'vanphong@gmail.com', N'Phó phòng', N'Không', N'11.000.000', N'Đã tuyển'),
(N'BCTD002', N'055 - Huỳnh Văn E', N'0123123132', N'huynhvanf@gmail.com', N'Chuyên viên SEO', N'Không', N'10.000.000', N'Chưa tuyển');

-- =========================
-- DỮ LIỆU MẪU NHÂN VIÊN
-- =========================
INSERT INTO Employees (EmployeeCode, FirstName, LastName, Email, Phone, Address, BirthDate, Gender, IdNumber, DepartmentId, PositionId, HireDate, Status, AvatarPath) VALUES
('002', N'Nguyễn Ngọc', N'Huy', NULL, '0121212445', N'157/89 Nguyễn Văn Linh,...', '2003-09-10', N'Nam', NULL, NULL, NULL, NULL, N'Nhân viên chính thức', NULL),
('003', N'Trần Ngọc Thảo', N'Ngân', NULL, '0200986711', N'157/89 Khóm 1, An Phú,...', '2003-06-09', N'Nữ', NULL, NULL, NULL, NULL, N'Nhân viên chính thức', NULL),
('004', N'Trương Thị Cẩm', N'Tú', NULL, '0121212445', N'157/89 Khóm 1, An Phú,...', '2003-05-03', N'Nữ', NULL, NULL, NULL, NULL, N'Nhân viên chính thức', NULL),
('007', N'Tăng Hồng Nguyên', N'Dân', NULL, '01414141414', N'157/89 Dương Bá Trạc,...', '2003-06-06', N'Nam', NULL, NULL, NULL, NULL, N'Nhân viên chính thức', NULL),
('008', N'Hồ Đỗ Hoàng', N'Khang', NULL, '0123123123', N'157/89 Khóm 1, Phú Hội,...', '2003-03-02', N'Nam', NULL, NULL, NULL, NULL, N'Nhân viên chính thức', NULL),
('009', N'Đỗ Thị Cẩm', N'Tiên', NULL, '0121212445', N'12A Khóm 1, An Phú,...', '2003-11-04', N'Nữ', NULL, NULL, NULL, NULL, N'Nhân viên chính thức', NULL),
('014', N'Nguyễn Thị Diễm', N'My', NULL, '0121212445', N'157/89 Dương Bá Trạc,...', '2003-01-23', N'Nữ', NULL, NULL, NULL, NULL, N'Nhân viên chính thức', NULL),
('016', N'Nguyễn Thị Mỹ', N'Nương', NULL, '01212121212', N'90/A Nguyễn Thị Tú, Bình...', '2003-01-14', N'Nữ', NULL, NULL, NULL, NULL, N'Nhân viên thử việc', NULL);
