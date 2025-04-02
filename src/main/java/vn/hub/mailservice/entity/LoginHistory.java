package vn.hub.mailservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "working_minutes")
    private Integer workingMinutes;

    @Column(name = "is_active")
    private boolean active;

    public LoginHistory(User user, LocalDateTime loginTime) {
        this.user = user;
        this.loginTime = loginTime;
        this.active = true;
    }

    public String getUsername() {
        return user != null ? user.getUsername() : null;
    }
}