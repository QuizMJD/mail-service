package vn.hub.mailservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hub.mailservice.dto.ApiResponse;
import vn.hub.mailservice.dto.LoginHistoryResponse;
import vn.hub.mailservice.dto.LoginRequest;
import vn.hub.mailservice.entity.LoginHistory;
import vn.hub.mailservice.service.LoginService;

import java.util.Map;

/**
 * Controller xử lý các request liên quan đến đăng nhập/đăng xuất
 * Cung cấp các API:
 * - Đăng nhập
 * - Đăng xuất
 * - Kiểm tra trạng thái đăng nhập
 * - Lấy thời gian làm việc
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * API đăng nhập
     * 
     * @param request Chứa username và password
     * @return Thông tin phiên đăng nhập nếu thành công
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginHistoryResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginHistory loginHistory = loginService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Đăng nhập thành công",
                new LoginHistoryResponse(loginHistory)));
    }

    /**
     * API đăng xuất
     * 
     * @param request Chứa username cần đăng xuất
     * @return Thông tin phiên đăng nhập đã đóng
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LoginHistoryResponse>> logout(@Valid @RequestBody LoginRequest request) {
        LoginHistory loginHistory = loginService.logout(request.getUsername());
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Đăng xuất thành công",
                new LoginHistoryResponse(loginHistory)));
    }

    /**
     * API kiểm tra trạng thái đăng nhập
     * 
     * @param username Username cần kiểm tra
     * @return Thông tin phiên đăng nhập nếu đang đăng nhập
     */
    @GetMapping("/check-login/{username}")
    public ResponseEntity<ApiResponse<LoginHistoryResponse>> checkLoginStatus(@PathVariable String username) {
        LoginHistory loginHistory = loginService.isUserLoggedIn(username);
        if (loginHistory != null) {
            return ResponseEntity.ok(new ApiResponse<>(
                    true,
                    "Người dùng đang đăng nhập",
                    new LoginHistoryResponse(loginHistory)));
        }
        return ResponseEntity.ok(new ApiResponse<>(
                false,
                "Người dùng chưa đăng nhập",
                null));
    }

    /**
     * API lấy thời gian làm việc trong ngày
     * 
     * @param username Username cần kiểm tra
     * @return Số phút làm việc trong ngày
     */
    @GetMapping("/working-hours/{username}")
    public ResponseEntity<ApiResponse<Integer>> getWorkingHours(@PathVariable String username) {
        int minutes = loginService.getTotalWorkingMinutesForToday(username);
        return ResponseEntity.ok(new ApiResponse<>(
                true,
                "Thời gian làm việc hôm nay",
                minutes));
    }
}