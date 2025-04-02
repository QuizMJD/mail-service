package vn.hub.mailservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.hub.mailservice.entity.LoginHistory;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistoryResponse {
    private String username;
    private LocalDateTime loginTime;
    private LocalDateTime logoutTime;
    private Integer workingMinutes;

    public LoginHistoryResponse(LoginHistory loginHistory) {
        this.username = loginHistory.getUser().getUsername();
        this.loginTime = loginHistory.getLoginTime();
        this.logoutTime = loginHistory.getLogoutTime();
        this.workingMinutes = loginHistory.getWorkingMinutes();
    }
}