package com.seriesapp.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.seriesapp.entity.Series;
import com.seriesapp.entity.User;
import com.seriesapp.repository.SeriesRepository;
import com.seriesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectMapper objectMapper;

    @Value("${app.admin.username}")
    private String adminUsername;
    @Value("${app.admin.username}")
    private String adminEmail;
    @Value("${app.admin.password}")
    private String adminPassword;


    @Override
    public void run(String... args) {
        initAdmin();
        initSeries();
    }

    private void initAdmin() {
        if (!userRepository.existsByUsername(adminUsername)) {
            User admin = User.builder()
                    .username(adminUsername)
                    .email(adminEmail)
                    .password(passwordEncoder.encode(adminPassword))
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Create Admin : {}", adminUsername);
        }
    }

    private void initSeries() {
        if (seriesRepository.count() == 0) {
            try {

                InputStream inputStream = new ClassPathResource("series-init.json").getInputStream();
                List<Series> samples = objectMapper.readValue(inputStream, new TypeReference<List<Series>>() {
                });

                if (!samples.isEmpty()) {
                    seriesRepository.saveAll(samples);
                    log.info("Successfully added {} series from JSON to DB", samples.size());
                }
            } catch (Exception e) {
                log.error("Failed to initialize series data from JSON", e);
            }
        }
    }
}

