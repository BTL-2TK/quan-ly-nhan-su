USE QuanLyNhanSu;
GO

-- Xóa bảng theo thứ tự khóa ngoại
IF OBJECT_ID('dbo.TaiKhoanNguoiDung', 'U') IS NOT NULL DROP TABLE dbo.TaiKhoanNguoiDung;
IF OBJECT_ID('dbo.Contracts', 'U') IS NOT NULL DROP TABLE dbo.Contracts;
IF OBJECT_ID('dbo.Employees', 'U') IS NOT NULL DROP TABLE dbo.Employees;
IF OBJECT_ID('dbo.UngVien', 'U') IS NOT NULL DROP TABLE dbo.UngVien;
IF OBJECT_ID('dbo.Departments', 'U') IS NOT NULL DROP TABLE dbo.Departments;
IF OBJECT_ID('dbo.VaiTro', 'U') IS NOT NULL DROP TABLE dbo.VaiTro;
IF OBJECT_ID('dbo.Positions', 'U') IS NOT NULL DROP TABLE dbo.Positions;
IF OBJECT_ID('dbo.Attendance', 'U') IS NOT NULL DROP TABLE dbo.Attendance;
GO

-- Bảng Vai trò
CREATE TABLE VaiTro (
    MaVaiTro INT IDENTITY(1,1) PRIMARY KEY,
    TenVaiTro NVARCHAR(50) NOT NULL
);
GO

-- Bảng Phòng ban
CREATE TABLE Departments (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    TenPhongBan NVARCHAR(100) NOT NULL
);
GO

-- Bảng Nhân viên
CREATE TABLE Employees (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    EmployeeCode NVARCHAR(50) NOT NULL UNIQUE,
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
    AvatarPath NVARCHAR(200),
    CONSTRAINT FK_Employees_Department FOREIGN KEY (DepartmentId) REFERENCES Departments(Id)
);
GO

-- Bảng Hợp đồng
CREATE TABLE Contracts (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    EmployeeId INT NOT NULL,
    EmployeeCode NVARCHAR(50) NOT NULL,
    ContractType NVARCHAR(50),
    StartDate DATE,
    EndDate DATE,
    Salary DECIMAL(18,2),
    Status NVARCHAR(20),
    Note NVARCHAR(200),
    CONSTRAINT FK_Contracts_Employee FOREIGN KEY (EmployeeId) REFERENCES Employees(Id),
    CONSTRAINT FK_Contracts_EmployeeCode FOREIGN KEY (EmployeeCode) REFERENCES Employees(EmployeeCode)
);
GO

-- Bảng Ứng viên
CREATE TABLE UngVien (
    MaTuyenDung NVARCHAR(50) NOT NULL PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai NVARCHAR(20),
    Email NVARCHAR(100),
    ChucVu NVARCHAR(50),
    TrinhDo NVARCHAR(50),
    MucLuongDeal NVARCHAR(50),
    TrangThai NVARCHAR(20) DEFAULT N'Chưa tuyển'
);
GO

-- Bảng Tài khoản người dùng
CREATE TABLE TaiKhoanNguoiDung (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    TenDangNhap NVARCHAR(50) NOT NULL UNIQUE,
    MatKhauHash NVARCHAR(256) NOT NULL,
    EmailXacThuc NVARCHAR(100),
    MaVaiTro INT,
    TrangThaiHoatDong BIT DEFAULT 1,
    CONSTRAINT FK_TaiKhoanNguoiDung_VaiTro FOREIGN KEY (MaVaiTro) REFERENCES VaiTro(MaVaiTro)
);
GO

-- Bảng Chức vụ (Positions)
CREATE TABLE Positions (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    PositionName NVARCHAR(100) NOT NULL
);
GO

-- Bảng chấm công
CREATE TABLE Attendance (
    Id INT IDENTITY(1,1) PRIMARY KEY,
    EmployeeCode NVARCHAR(50) NOT NULL,
    WorkDate DATE NOT NULL,
    Status NVARCHAR(20) NOT NULL,
    CONSTRAINT FK_Attendance_EmployeeCode FOREIGN KEY (EmployeeCode) REFERENCES Employees(EmployeeCode)
);
GO

-- Dữ liệu mẫu Vai trò (chỉ 3 role)
INSERT INTO VaiTro (TenVaiTro) VALUES (N'ADMIN'), (N'USER'), (N'HR_MANAGER');
GO

-- Dữ liệu mẫu Phòng ban
INSERT INTO Departments (TenPhongBan) VALUES
(N'Phòng kĩ thuật công nghệ'),
(N'Phòng kinh doanh'),
(N'Phòng nghiên cứu và phát triển'),
(N'Phòng nhân sự'),
(N'Phòng tài chính kế toán'),
(N'Phòng truyền thông marketing'),
(N'Phòng tuyển dụng');
GO

-- Dữ liệu mẫu Chức vụ
INSERT INTO Positions (PositionName) VALUES
(N'Giám đốc'),
(N'Trưởng phòng Nhân sự'),
(N'Trưởng phòng Kỹ thuật'),
(N'Trưởng phòng Kinh doanh'),
(N'Nhân viên');
GO

-- Dữ liệu mẫu Nhân viên
INSERT INTO Employees (EmployeeCode, FirstName, LastName, Email, Phone, Address, BirthDate, Gender, IdNumber, DepartmentId, PositionId, HireDate, Status, AvatarPath) VALUES
('001', N'Nguyễn', N'Văn A', 'a.nguyen@abc.com', '0901234567', N'123 Đường Lớn, Quận 1', '1980-01-01', N'Nam', '123456789', 1, 1, '2010-01-01', N'Chính thức', NULL), -- Giám đốc
('002', N'Trần', N'Thị B', 'b.tran@abc.com', '0902345678', N'234 Đường Lớn, Quận 1', '1982-02-02', N'Nữ', '234567890', 4, 2, '2012-02-02', N'Chính thức', NULL), -- Trưởng phòng Nhân sự
('003', N'Lê', N'Văn C', 'c.le@abc.com', '0903456789', N'345 Đường Lớn, Quận 1', '1984-03-03', N'Nam', '345678901', 1, 3, '2014-03-03', N'Chính thức', NULL), -- Trưởng phòng Kỹ thuật
('004', N'Phạm', N'Thị D', 'd.pham@abc.com', '0904567890', N'456 Đường Lớn, Quận 1', '1986-04-04', N'Nữ', '456789012', 2, 4, '2016-04-04', N'Chính thức', NULL), -- Trưởng phòng Kinh doanh
('005', N'Ngô', N'Nhân Viên', 'nv.ngo@abc.com', '0905678901', N'567 Đường Lớn, Quận 1', '1990-05-05', N'Nam', '567890123', 1, 5, '2020-05-05', N'Chính thức', NULL), -- Nhân viên thường
('006', N'Hoàng', N'Thị E', 'e.hoang@abc.com', '0906789012', N'678 Đường Lớn, Quận 2', '1992-06-06', N'Nữ', '678901234', 2, 6, '2018-06-06', N'Chính thức', NULL),
('007', N'Phan', N'Văn F', 'f.phan@abc.com', '0907890123', N'789 Đường Lớn, Quận 3', '1994-07-07', N'Nam', '789012345', 3, 7, '2019-07-07', N'Chính thức', NULL),
('008', N'Vũ', N'Thị G', 'g.vu@abc.com', '0908901234', N'890 Đường Lớn, Quận 4', '1996-08-08', N'Nữ', '890123456', 5, 8, '2020-08-08', N'Chính thức', NULL),
('009', N'Đỗ', N'Văn H', 'h.do@abc.com', '0909012345', N'901 Đường Lớn, Quận 5', '1998-09-09', N'Nam', '901234567', 6, 9, '2021-09-09', N'Chính thức', NULL),
('010', N'Bùi', N'Thị I', 'i.bui@abc.com', '0910123456', N'012 Đường Lớn, Quận 6', '2000-10-10', N'Nữ', '012345678', 7, 10, '2022-10-10', N'Chính thức', NULL);
GO

-- Dữ liệu mẫu Hợp đồng
INSERT INTO Contracts (EmployeeId, EmployeeCode, ContractType, StartDate, EndDate, Salary, Status, Note) VALUES
(1, '001', N'Chính thức', '2022-01-01', '2023-01-01', 10000000, N'Còn hiệu lực', N'Hợp đồng 1'),
(2, '002', N'Thử việc', '2022-02-01', '2022-08-01', 8000000, N'Hết hạn', N'Hợp đồng 2'),
(3, '003', N'Chính thức', '2021-05-01', '2022-05-01', 12000000, N'Còn hiệu lực', N'Hợp đồng 3'),
(4, '004', N'Chính thức', '2022-03-01', '2023-03-01', 11000000, N'Còn hiệu lực', N'Hợp đồng 4'),
(5, '005', N'Chính thức', '2022-04-01', '2023-04-01', 9000000, N'Còn hiệu lực', N'Hợp đồng 5'),
(6, '006', N'Chính thức', '2022-05-01', '2023-05-01', 9500000, N'Còn hiệu lực', N'Hợp đồng 6'),
(7, '007', N'Chính thức', '2022-06-01', '2023-06-01', 10500000, N'Còn hiệu lực', N'Hợp đồng 7'),
(8, '008', N'Chính thức', '2022-07-01', '2023-07-01', 9800000, N'Còn hiệu lực', N'Hợp đồng 8'),
(9, '009', N'Chính thức', '2022-08-01', '2023-08-01', 10200000, N'Còn hiệu lực', N'Hợp đồng 9'),
(10, '010', N'Chính thức', '2022-09-01', '2023-09-01', 9700000, N'Còn hiệu lực', N'Hợp đồng 10');
GO

-- Dữ liệu mẫu Ứng viên
INSERT INTO UngVien (MaTuyenDung, HoTen, SoDienThoai, Email, ChucVu, TrinhDo, MucLuongDeal, TrangThai) VALUES
(N'BCTD001', N'Nguyễn Văn A', N'0901000001', N'a1@gmail.com', N'Nhân viên', N'Đại học', N'10.000.000', N'Chưa tuyển'),
(N'BCTD002', N'Lê Thị B', N'0901000002', N'b2@gmail.com', N'Kỹ sư', N'Cao đẳng', N'12.000.000', N'Đã tuyển'),
(N'BCTD003', N'Trần Văn C', N'0901000003', N'c3@gmail.com', N'Quản lý', N'Đại học', N'15.000.000', N'Chưa tuyển'),
(N'BCTD004', N'Nguyễn Thị D', N'0901000004', N'd4@gmail.com', N'Nhân viên', N'Đại học', N'11.000.000', N'Chưa tuyển'),
(N'BCTD005', N'Lê Văn E', N'0901000005', N'e5@gmail.com', N'Kỹ sư', N'Cao đẳng', N'13.000.000', N'Chưa tuyển'),
(N'BCTD006', N'Trần Thị F', N'0901000006', N'f6@gmail.com', N'Quản lý', N'Đại học', N'16.000.000', N'Chưa tuyển'),
(N'BCTD007', N'Phạm Văn G', N'0901000007', N'g7@gmail.com', N'Nhân viên', N'Đại học', N'10.500.000', N'Chưa tuyển'),
(N'BCTD008', N'Vũ Thị H', N'0901000008', N'h8@gmail.com', N'Kỹ sư', N'Cao đẳng', N'12.500.000', N'Chưa tuyển'),
(N'BCTD009', N'Đỗ Văn I', N'0901000009', N'i9@gmail.com', N'Quản lý', N'Đại học', N'15.500.000', N'Chưa tuyển'),
(N'BCTD010', N'Bùi Thị K', N'0901000010', N'k10@gmail.com', N'Nhân viên', N'Đại học', N'11.500.000', N'Chưa tuyển');
GO

-- Dữ liệu mẫu Tài khoản người dùng (mật khẩu là '123456' đã hash SHA-256)
INSERT INTO TaiKhoanNguoiDung (TenDangNhap, MatKhauHash, EmailXacThuc, MaVaiTro, TrangThaiHoatDong) VALUES
('admin', '8d969eef6ecad3c29a3a629280e686cff8fab2e5c7bfb3b06b2cd6fa5e7b6a58', 'admin@company.com', 1, 1),
('user1', '8d969eef6ecad3c29a3a629280e686cff8fab2e5c7bfb3b06b2cd6fa5e7b6a58', 'user1@company.com', 2, 1),
('hr1', '8d969eef6ecad3c29a3a629280e686cff8fab2e5c7bfb3b06b2cd6fa5e7b6a58', 'hr1@company.com', 3, 1);
GO
