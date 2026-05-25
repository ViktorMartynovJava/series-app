package com.seriesapp.config;

import com.seriesapp.entity.Series;
import com.seriesapp.entity.User;
import com.seriesapp.repository.SeriesRepository;
import com.seriesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SeriesRepository seriesRepository;
    private final PasswordEncoder passwordEncoder;

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
            // Твой список сериалов
            List<Series> samples = List.of(
                    Series.builder()
                            .title("Прямиком в ад")
                            .genre("Драма, биография, история")
                            .year(2026)
                            .country("Japan")
                            .imdbRating(8.5)
                            .episodesCount(9)
                            .description("Кто она: мошенница или святая? Биографическая драма о самой скандальной телегадалке Японии")
                            .posterUrl("/images/seriesfirst.jpg")
                            .status(Series.Status.COMPLETED)
                            .trailerUrl("api/videos/trailer")
                            .build()
            );

            if (!samples.isEmpty()) {
                seriesRepository.saveAll(samples);
                log.info("Add series on DB ", samples.size());
            }
        }
    }
}
