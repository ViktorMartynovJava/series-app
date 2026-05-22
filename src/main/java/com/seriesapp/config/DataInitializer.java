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


                List<java.util.Map<String, Object>> rawList = objectMapper.readValue(
                        inputStream, new TypeReference<List<java.util.Map<String, Object>>>() {
                        }
                );

                List<Series> samples = rawList.stream().map(raw -> {
                    Series series = new Series();
                    series.setTitle((String) raw.get("title"));
                    series.setDescription((String) raw.get("description"));
                    series.setPosterUrl((String) raw.get("posterUrl"));
                    series.setTrailerUrl((String) raw.get("trailerUrl"));
                    series.setVideoUrl((String) raw.get("videoUrl"));
                    series.setCountry((String) raw.get("country"));
                    series.setYear((Integer) raw.get("year"));
                    series.setEpisodesCount((Integer) raw.get("episodesCount"));

                    if (raw.get("imdbRating") != null) {
                        series.setImdbRating(((Number) raw.get("imdbRating")).doubleValue());
                    }

                    Object genresRaw = raw.get("genres");
                    if (genresRaw instanceof List<?> genreList) {
                        String joined = genreList.stream()
                                .map(Object::toString)
                                .collect(java.util.stream.Collectors.joining(", "));
                        series.setGenre(joined);
                    }

                    if (raw.get("status") != null) {
                        series.setStatus(Series.Status.valueOf((String) raw.get("status")));
                    } else {
                        series.setStatus(Series.Status.ONGOING);
                    }

                    return series;
                }).toList();

                seriesRepository.saveAll(samples);
                log.info("Successfully added {} series from JSON to DB", samples.size());

            } catch (Exception e) {
                log.error("Failed to initialize series data from JSON", e);
            }
        }
    }
}

