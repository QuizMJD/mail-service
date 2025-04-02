# DỊCH VỤ GỬI EMAIL

## Giới thiệu

Đây là dịch vụ (microservice) gửi email đơn giản được xây dựng bằng Spring Boot. Dịch vụ này cung cấp các chức năng:

- Gửi email HTML đơn giản cho một người
- Gửi email cho nhiều người dùng CC
- Gửi email cho nhiều người dùng BCC
- Gửi email với mã QR code thanh toán
- Gửi email kèm file PDF đính kèm

## Luồng chạy chương trình

### 1. Gửi Email HTML đơn giản

```
Client → EmailController → EmailService → JavaMailSender → SMTP Server
```

- Client gọi API `POST /api/emails/send-html` với thông tin email
- Controller nhận request và chuyển cho EmailService
- EmailService sử dụng JavaMailSender để gửi email qua SMTP

### 2. Gửi Email có CC/BCC

```
Client → EmailController → EmailService → JavaMailSender → SMTP Server
```

- Client gọi API `POST /api/emails/send-with-cc` hoặc `POST /api/emails/send-with-bcc`
- EmailService thiết lập danh sách CC/BCC và gửi email

### 3. Gửi Email có QR Code

```
Client → EmailController → EmailService → QRCodeGenerator → JavaMailSender → SMTP Server
```

- Client gọi API `POST /api/emails/send-with-qrcode`
- EmailService sử dụng ZXing để tạo QR code
- QR code được nhúng vào email dưới dạng inline image

### 4. Gửi Email có đính kèm PDF

```
Client → EmailController → EmailService → JavaMailSender → SMTP Server
```

- Client gọi API `POST /api/emails/send-with-pdf` và upload file PDF
- EmailService đính kèm file PDF vào email và gửi đi

## Các bước triển khai

### Bước 1: Chuẩn bị môi trường

- JDK 17 hoặc cao hơn
- Maven 3.6+ hoặc Gradle 7+
- Tài khoản email cho SMTP (Gmail, Outlook, v.v.)

### Bước 2: Clone và cấu hình dự án

1. Clone dự án:
   ```bash
   git clone <repository-url>
   cd mail-service
   ```

2. Cấu hình thông tin SMTP trong `src/main/resources/application.properties`:
   ```properties
   spring.mail.host=smtp.gmail.com
   spring.mail.port=587
   spring.mail.username=your-email@gmail.com
   spring.mail.password=your-app-password
   spring.mail.properties.mail.smtp.auth=true
   spring.mail.properties.mail.smtp.starttls.enable=true
   ```

   > **Lưu ý**: Đối với Gmail, bạn cần sử dụng "App Password" thay vì mật khẩu thông thường. Xem hướng dẫn tạo App Password [tại đây](https://support.google.com/accounts/answer/185833).

### Bước 3: Biên dịch dự án

```bash
mvn clean package
```

### Bước 4: Chạy ứng dụng

```bash
mvn spring-boot:run
```

hoặc

```bash
java -jar target/mail-service-0.0.1-SNAPSHOT.jar
```

## Cách test ứng dụng

### 1. Test API gửi email đơn giản

```bash
curl -X POST http://localhost:8080/api/emails/send-html \
  -H "Content-Type: application/json" \
  -d '{
    "to": "recipient@example.com",
    "subject": "Thông báo mới",
    "htmlContent": "<h1>Xin chào</h1><p>Đây là email test</p>"
  }'
```

### 2. Test API gửi email với CC

```bash
curl -X POST http://localhost:8080/api/emails/send-with-cc \
  -H "Content-Type: application/json" \
  -d '{
    "to": "main@example.com",
    "ccList": ["cc1@example.com", "cc2@example.com"],
    "subject": "Thông báo cho nhóm",
    "htmlContent": "<h1>Thông báo nhóm</h1><p>Gửi tới tất cả</p>"
  }'
```

### 3. Test API gửi email với BCC

```bash
curl -X POST http://localhost:8080/api/emails/send-with-bcc \
  -H "Content-Type: application/json" \
  -d '{
    "to": "main@example.com",
    "bccList": ["hidden1@example.com", "hidden2@example.com"],
    "subject": "Thông báo bí mật",
    "htmlContent": "<h1>Thông báo bí mật</h1><p>Nội dung bảo mật</p>"
  }'
```

### 4. Test API gửi email với QR Code

```bash
curl -X POST http://localhost:8080/api/emails/send-with-qrcode \
  -H "Content-Type: application/json" \
  -d '{
    "to": "customer@example.com",
    "subject": "Thanh toán hóa đơn",
    "htmlContent": "<h1>Thông tin thanh toán</h1><p>Quét mã QR để thanh toán:</p><img src=\"cid:qrcode\">",
    "qrCodeContent": "https://example.com/pay?id=12345&amount=100000"
  }'
```

### 5. Test API gửi email với PDF đính kèm

Sử dụng công cụ như Postman hoặc HTML form để upload file:

```bash
curl -X POST http://localhost:8080/api/emails/send-with-pdf \
  -F "to=recipient@example.com" \
  -F "subject=Tài liệu quan trọng" \
  -F "htmlContent=<h1>Tài liệu đính kèm</h1><p>Vui lòng xem file đính kèm</p>" \
  -F "pdfFile=@/đường/dẫn/đến/file.pdf"
```

### 6. Test bằng Swagger UI (nếu có)

Sau khi chạy ứng dụng, truy cập:
```
http://localhost:8080/swagger-ui.html
```

## Cấu trúc chính của dự án

```
src/main/java/vn/hub/mailservice/
├── controller/
│   └── EmailController.java
├── service/
│   ├── EmailService.java (interface)
│   └── impl/
│       ├── EmailServiceImpl.java
│       └── NotificationService.java
├── dto/
│   ├── EmailRequest.java
│   ├── MultipleRecipientsEmailRequest.java
│   └── QRCodeEmailRequest.java
├── util/
│   └── TemplateUtil.java
└── MailServiceApplication.java

src/main/resources/
├── templates/
│   ├── notification-email.html
│   └── payment-email.html
└── application.properties
```

## Các lỗi thường gặp và cách khắc phục

1. **Lỗi authentication SMTP**:
   - Kiểm tra lại username và password
   - Đối với Gmail, cần sử dụng App Password và bật "Less secure app access"

2. **Lỗi kết nối SMTP**:
   - Kiểm tra cấu hình host và port
   - Đảm bảo không có firewall chặn kết nối

3. **File đính kèm quá lớn**:
   - Tăng giá trị `spring.servlet.multipart.max-file-size` và `spring.servlet.multipart.max-request-size` trong `application.properties`

## Mở rộng dự án

Để mở rộng dự án, bạn có thể:

1. Thêm cơ sở dữ liệu để lưu trữ lịch sử email đã gửi
2. Tích hợp với template engine để tạo email động
3. Thêm chức năng gửi email định kỳ bằng Spring Scheduler
4. Thêm retry mechanism cho các email gửi thất bại

## Tham khảo

- [Spring Email Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#mail)
- [Jakarta Mail API](https://jakarta.ee/specifications/mail/) 