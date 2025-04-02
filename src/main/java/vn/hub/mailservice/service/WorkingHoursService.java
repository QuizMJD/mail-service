package vn.hub.mailservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import vn.hub.mailservice.entity.LoginHistory;
import vn.hub.mailservice.entity.User;
import vn.hub.mailservice.repository.LoginHistoryRepository;
import vn.hub.mailservice.repository.UserRepository;
import vn.hub.mailservice.service.EmailService;
import vn.hub.mailservice.util.TemplateUtil;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service xử lý tính toán giờ làm việc và gửi thông báo
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class WorkingHoursService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final EmailService emailService;
    private final TemplateUtil templateUtil;

    // Thời gian kết thúc làm việc (7:30 PM)
    private static final LocalTime WORK_END_TIME = LocalTime.of(19, 30);
    // Số phút làm việc yêu cầu (8 giờ = 480 phút)
    private static final int REQUIRED_MINUTES = 480;

    /**
     * Job tự động chạy lúc 9h tối hàng ngày để kiểm tra và thông báo giờ làm việc
     */
    @Scheduled(cron = "0 0 21 * * *") // Chạy lúc 9 giờ tối hàng ngày
    public void calculateAndNotifyWorkingHours() {
        log.info("Bắt đầu tính toán giờ làm việc cho ngày {}", LocalDateTime.now().toLocalDate());

        try {
            // Lấy danh sách user đang đăng nhập
            List<User> activeUsers = userRepository.findByActiveTrue();

            for (User user : activeUsers) {
                try {
                    // Tính thời gian làm việc của user
                    int workingMinutes = calculateWorkingMinutes(user);

                    // Nếu không đủ 8 tiếng, gửi thông báo
                    if (workingMinutes < REQUIRED_MINUTES) {
                        sendWorkingHoursNotification(user.getUsername(), workingMinutes);
                    }
                } catch (Exception e) {
                    log.error("Lỗi khi xử lý user {}: {}", user.getUsername(), e.getMessage());
                }
            }

            log.info("Hoàn thành tính toán giờ làm việc");
        } catch (Exception e) {
            log.error("Lỗi khi tính toán giờ làm việc: {}", e.getMessage());
        }
    }

    /**
     * Tính tổng thời gian làm việc của user trong ngày
     * 
     * @param user User cần tính
     * @return Số phút làm việc
     */
    private int calculateWorkingMinutes(User user) {
        // Lấy thời gian đầu ngày và cuối ngày
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59);

        // Tính tổng thời gian làm việc
        return loginHistoryRepository.calculateTotalWorkingMinutes(user.getId(), startOfDay, endOfDay);
    }

    /**
     * Gửi email thông báo cho user không đủ giờ làm việc
     * 
     * @param username      Username của user
     * @param actualMinutes Số phút làm việc thực tế
     */
    private void sendWorkingHoursNotification(String username, int actualMinutes) {
        // Tính số phút thiếu
        int missingMinutes = REQUIRED_MINUTES - actualMinutes;

        // Chuẩn bị dữ liệu cho template
        Map<String, String> variables = new HashMap<>();
        variables.put("name", username);
        variables.put("actualHours", String.format("%.2f", actualMinutes / 60.0));
        variables.put("missingMinutes", String.valueOf(missingMinutes));

        // Xử lý template và gửi email
        String htmlContent = templateUtil.processTemplate("templates/working-hours-notification.html", variables);
        emailService.sendHtmlEmail(
                username,
                "Thông báo giờ làm việc không đủ",
                htmlContent);

        log.info("Đã gửi thông báo cho user {} về việc thiếu {} phút làm việc", username, missingMinutes);
    }
}