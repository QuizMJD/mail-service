# Mail Service - Hệ thống Quản lý Đăng nhập và Thông báo Giờ làm việc

## Tổng quan
Hệ thống này cung cấp các chức năng:
1. Quản lý đăng nhập/đăng xuất của nhân viên
2. Tính toán thời gian làm việc
3. Tự động gửi thông báo khi nhân viên không đủ giờ làm việc

## Cấu trúc Project
```
src/main/java/vn/hub/mailservice/
├── config/           # Cấu hình hệ thống
├── controller/       # Xử lý request
├── dto/             # Data Transfer Objects
├── entity/          # Entity classes
├── repository/      # Data access layer
├── service/         # Business logic
└── util/            # Utility classes
```

## Luồng xử lý chính

### 1. Đăng nhập (Login)
1. Client gửi request POST `/api/login` với username và password
2. `LoginController` nhận request và chuyển cho `LoginService`
3. `LoginService` kiểm tra:
   - User tồn tại
   - Password đúng
   - Tài khoản active
   - Không có phiên đăng nhập nào đang active
4. Tạo `LoginHistory` mới với thời gian đăng nhập
5. Trả về thông tin phiên đăng nhập

### 2. Đăng xuất (Logout)
1. Client gửi request POST `/api/logout` với username
2. `LoginController` nhận request và chuyển cho `LoginService`
3. `LoginService`:
   - Tìm phiên đăng nhập active
   - Cập nhật thời gian đăng xuất
   - Tính toán thời gian làm việc
4. Trả về thông tin phiên đăng nhập đã đóng

### 3. Kiểm tra trạng thái đăng nhập
1. Client gửi request GET `/api/check-login/{username}`
2. `LoginController` chuyển request cho `LoginService`
3. `LoginService` kiểm tra phiên đăng nhập active
4. Trả về thông tin phiên đăng nhập nếu có

### 4. Tính toán giờ làm việc
1. Client gửi request GET `/api/working-hours/{username}`
2. `LoginController` chuyển request cho `LoginService`
3. `LoginService`:
   - Lấy tất cả phiên đăng nhập trong ngày
   - Tính tổng thời gian làm việc
4. Trả về số phút làm việc

### 5. Tự động thông báo giờ làm việc
1. Job tự động chạy lúc 9h tối hàng ngày
2. `WorkingHoursService`:
   - Lấy danh sách user đang đăng nhập
   - Tính thời gian làm việc của từng user
   - Nếu không đủ 8 tiếng:
     + Tính số phút thiếu
     + Gửi email thông báo

## Hướng dẫn Implement từ đầu

### Bước 1: Setup Project
1. Tạo Spring Boot project với các dependency:
   - spring-boot-starter-web
   - spring-boot-starter-data-jpa
   - spring-boot-starter-mail
   - h2database
   - lombok

### Bước 2: Cấu hình Database
1. Cấu hình H2 Database trong `application.yml`
2. Tạo các entity: User, LoginHistory
3. Tạo các repository tương ứng

### Bước 3: Implement Core Features
1. Tạo các DTO cho request/response
2. Implement LoginService với các chức năng:
   - login
   - logout
   - check login status
   - calculate working hours
3. Implement LoginController để xử lý các API

### Bước 4: Implement Email Service
1. Cấu hình SMTP trong `application.yml`
2. Tạo email template
3. Implement EmailService để gửi mail

### Bước 5: Implement Scheduled Task
1. Thêm @EnableScheduling
2. Implement job tính toán giờ làm việc
3. Thêm logic gửi thông báo

### Bước 6: Testing
1. Test các API với Postman
2. Test job tự động
3. Test gửi email

## API Documentation

### Login
```
POST /api/login
Request:
{
    "username": "string",
    "password": "string"
}
Response:
{
    "success": true,
    "message": "Đăng nhập thành công",
    "data": {
        "username": "string",
        "loginTime": "2024-03-21T10:00:00",
        "logoutTime": null,
        "workingMinutes": 0
    }
}
```

### Logout
```
POST /api/logout
Request:
{
    "username": "string"
}
Response:
{
    "success": true,
    "message": "Đăng xuất thành công",
    "data": {
        "username": "string",
        "loginTime": "2024-03-21T10:00:00",
        "logoutTime": "2024-03-21T18:00:00",
        "workingMinutes": 480
    }
}
```

### Check Login Status
```
GET /api/check-login/{username}
Response:
{
    "success": true,
    "message": "Người dùng đang đăng nhập",
    "data": {
        "username": "string",
        "loginTime": "2024-03-21T10:00:00",
        "logoutTime": null,
        "workingMinutes": 0
    }
}
```

### Get Working Hours
```
GET /api/working-hours/{username}
Response:
{
    "success": true,
    "message": "Thời gian làm việc hôm nay",
    "data": 480
}
```