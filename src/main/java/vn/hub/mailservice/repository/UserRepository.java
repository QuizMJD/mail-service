package vn.hub.mailservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.hub.mailservice.entity.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    List<User> findByActiveTrue();
}