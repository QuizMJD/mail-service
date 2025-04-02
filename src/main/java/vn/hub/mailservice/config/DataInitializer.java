package vn.hub.mailservice.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.hub.mailservice.entity.User;
import vn.hub.mailservice.repository.UserRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.findByUsername("admin").isPresent()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword("admin123");
            adminUser.setEmail("admin@example.com");
            adminUser.setFullName("Admin User");
            adminUser.setActive(true);
            userRepository.save(adminUser);
        }
    }
}