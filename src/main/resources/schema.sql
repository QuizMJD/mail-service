-- Xóa bảng nếu tồn tại
DROP TABLE IF EXISTS login_history;
DROP TABLE IF EXISTS users;

-- Tạo bảng users
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    full_name VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE
);

-- Tạo bảng login_history
CREATE TABLE login_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    login_time DATETIME NOT NULL,
    logout_time DATETIME,
    working_minutes INT,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id)
); 