package com.cts.regreportx.config;

import com.cts.regreportx.model.User;
import com.cts.regreportx.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() == 0) {
                User admin = new User();
                admin.setName("DILIP");
                admin.setUsername("Dilipkumar11");
                admin.setEmail("Dilipkumar11@cognizant.com");
                admin.setPassword(passwordEncoder.encode("1234@"));
                admin.setRole("REGTECH_ADMIN");
                admin.setStatus("ACTIVE");
                admin.setCreatedAt(java.time.LocalDateTime.parse("2026-03-14T14:49:35.405"));
                userRepository.save(admin);
                System.out.println("Inserted default admin user (DILIP) with role REGTECH_ADMIN.");
            }
        };
    }
}
