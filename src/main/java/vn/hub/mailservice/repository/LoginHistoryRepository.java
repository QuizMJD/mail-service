package vn.hub.mailservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.hub.mailservice.entity.LoginHistory;
import vn.hub.mailservice.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LoginHistoryRepository extends JpaRepository<LoginHistory, Long> {

        Optional<LoginHistory> findByUserAndLogoutTimeIsNull(User user);

        List<LoginHistory> findAllByLoginTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

        @Query("SELECT COALESCE(SUM(lh.workingMinutes), 0) FROM LoginHistory lh " +
                        "WHERE lh.user.id = :userId AND lh.loginTime BETWEEN :startTime AND :endTime")
        int calculateTotalWorkingMinutes(@Param("userId") Long userId,
                        @Param("startTime") LocalDateTime startTime,
                        @Param("endTime") LocalDateTime endTime);
}