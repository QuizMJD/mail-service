package vn.hub.mailservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.hub.mailservice.entity.LoginHistory;
import vn.hub.mailservice.entity.User;
import vn.hub.mailservice.repository.LoginHistoryRepository;
import vn.hub.mailservice.repository.UserRepository;
import vn.hub.mailservice.service.EmailService;
import vn.hub.mailservice.service.LoginService;
import vn.hub.mailservice.util.TemplateUtil;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service xử lý logic đăng nhập/đăng xuất và tính toán thời gian làm việc
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final EmailService emailService;
    private final TemplateUtil templateUtil;

    private static final LocalTime WORK_END_TIME = LocalTime.of(19, 30); // 7:30 PM
    private static final int REQUIRED_MINUTES = 480; // 8 hours = 480 minutes

    /**
     * Xử lý đăng nhập cho user
     * 
     * @param username Tên đăng nhập
     * @param password Mật khẩu
     * @return LoginHistory chứa thông tin phiên đăng nhập
     * @throws RuntimeException nếu:
     *                          - User không tồn tại
     *                          - Password không đúng
     *                          - Tài khoản bị khóa
     *                          - User đã có phiên đăng nhập active
     */
    @Override
    public LoginHistory login(String username, String password) {
        // Tìm user theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra password
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        // Kiểm tra trạng thái tài khoản
        if (!user.isActive()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        // Kiểm tra xem người dùng đã đăng nhập chưa
        LoginHistory activeSession = loginHistoryRepository.findByUserAndLogoutTimeIsNull(user)
                .orElse(null);
        if (activeSession != null) {
            throw new RuntimeException("Người dùng đã đăng nhập");
        }

        // Tạo phiên đăng nhập mới
        LoginHistory loginHistory = new LoginHistory(user, LocalDateTime.now());
        return loginHistoryRepository.save(loginHistory);
    }

    /**
     * Xử lý đăng xuất cho user
     * 
     * @param username Tên đăng nhập
     * @return LoginHistory đã cập nhật thời gian đăng xuất
     * @throws RuntimeException nếu:
     *                          - User không tồn tại
     *                          - Không tìm thấy phiên đăng nhập active
     */
    @Override
    public LoginHistory logout(String username) {
        // Tìm user theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Tìm phiên đăng nhập active
        LoginHistory loginHistory = loginHistoryRepository.findByUserAndLogoutTimeIsNull(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiên đăng nhập"));

        // Cập nhật thời gian đăng xuất và tính thời gian làm việc
        LocalDateTime now = LocalDateTime.now();
        loginHistory.setLogoutTime(now);
        loginHistory.setWorkingMinutes((int) ChronoUnit.MINUTES.between(loginHistory.getLoginTime(), now));
        loginHistory.setActive(false);

        return loginHistoryRepository.save(loginHistory);
    }

    /**
     * Kiểm tra trạng thái đăng nhập của user
     * 
     * @param username Tên đăng nhập
     * @return LoginHistory nếu user đang đăng nhập, null nếu không
     * @throws RuntimeException nếu user không tồn tại
     */
    @Override
    public LoginHistory isUserLoggedIn(String username) {
        // Tìm user theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Tìm phiên đăng nhập active
        return loginHistoryRepository.findByUserAndLogoutTimeIsNull(user)
                .orElse(null);
    }

    /**
     * Tính tổng thời gian làm việc của user trong ngày
     * 
     * @param username Tên đăng nhập
     * @return Số phút làm việc trong ngày
     * @throws RuntimeException nếu user không tồn tại
     */
    @Override
    public int getTotalWorkingMinutesForToday(String username) {
        // Tìm user theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Lấy thời gian đầu ngày và cuối ngày
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Tính tổng thời gian làm việc
        return loginHistoryRepository.calculateTotalWorkingMinutes(
                user.getId(), startOfDay, endOfDay);
    }

    @Override
    @Scheduled(cron = "0 0 21 * * *") // Chạy lúc 9 giờ tối hàng ngày
    public void calculateAndNotifyWorkingHours() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        List<LoginHistory> todayLogins = loginHistoryRepository.findAllByLoginTimeBetween(startOfDay, endOfDay);

        // Nhóm theo username và tính tổng thời gian
        Map<String, Integer> userWorkingMinutes = new HashMap<>();
        for (LoginHistory login : todayLogins) {
            userWorkingMinutes.merge(
                    login.getUsername(),
                    login.getWorkingMinutes() != null ? login.getWorkingMinutes() : 0,
                    Integer::sum);
        }

        // Gửi thông báo cho những người không đủ 8 tiếng
        userWorkingMinutes.forEach((username, minutes) -> {
            if (minutes < REQUIRED_MINUTES) {
                sendWorkingHoursNotification(username, minutes);
            }
        });
    }

    private void sendWorkingHoursNotification(String username, int actualMinutes) {
        int missingMinutes = REQUIRED_MINUTES - actualMinutes;
        Map<String, String> variables = new HashMap<>();
        variables.put("name", username);
        variables.put("actualHours", String.format("%.2f", actualMinutes / 60.0));
        variables.put("missingMinutes", String.valueOf(missingMinutes));

        String htmlContent = templateUtil.processTemplate("templates/working-hours-notification.html", variables);
        emailService.sendHtmlEmail(
                username,
                "Thông báo giờ làm việc không đủ",
                htmlContent);
    }
}