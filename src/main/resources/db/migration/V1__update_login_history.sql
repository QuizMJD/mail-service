-- Xóa bảng cũ nếu tồn tại
DROP TABLE IF EXISTS login_history;

-- Tạo lại bảng với cấu trúc mới
CREATE TABLE login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_time DATETIME NOT NULL,
    logout_time DATETIME,
    working_minutes INT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
); 