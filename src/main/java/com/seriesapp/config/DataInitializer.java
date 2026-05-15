package com.seriesapp.config;

import com.seriesapp.entity.Series;
import com.seriesapp.entity.User;
import com.seriesapp.repository.SeriesRepository;
import com.seriesapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @Override
    public void run(String... args) {
        // Create admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                    .username("admin")
                    .email("admin@seriesapp.com")
                    .password(passwordEncoder.encode("admin123"))
                    .role(User.Role.ADMIN)
                    .build();
            userRepository.save(admin);
            log.info("Admin created: admin / admin123");
        }

        // Create sample series
        if (seriesRepository.count() == 0) {
            List<Series> samples = List.of(
                Series.builder()
                    .title("Breaking Bad")
                    .description("Учитель химии Уолтер Уайт узнаёт, что болен раком лёгких. Чтобы обеспечить семью, он начинает варить метамфетамин.")
                    .genre("Криминал")
                    .year(2008)
                    .country("США")
                    .imdbRating(9.5)
                    .status(Series.Status.COMPLETED)
                    .episodesCount(62)
                    .posterUrl("https://upload.wikimedia.org/wikipedia/en/6/61/Breaking_Bad_title_card.png")
                    .trailerUrl("https://www.youtube.com/watch?v=HhesaQXLuRY")
                    .build(),
                Series.builder()
                    .title("Игра Престолов")
                    .description("Эпическое фэнтезийное шоу о борьбе за Железный Трон Семи Королевств Вестероса.")
                    .genre("Фэнтези")
                    .year(2011)
                    .country("США")
                    .imdbRating(9.2)
                    .status(Series.Status.COMPLETED)
                    .episodesCount(73)
                    .posterUrl("https://upload.wikimedia.org/wikipedia/en/d/d8/Game_of_Thrones_season_4.jpg")
                    .build(),
                Series.builder()
                    .title("Чернобыль")
                    .description("Минисериал о катастрофе на Чернобыльской АЭС в 1986 году и людях, которые рисковали жизнью.")
                    .genre("Драма")
                    .year(2019)
                    .country("США / Великобритания")
                    .imdbRating(9.4)
                    .status(Series.Status.COMPLETED)
                    .episodesCount(5)
                    .posterUrl("https://upload.wikimedia.org/wikipedia/en/4/44/Chernobyl_title_card.jpg")
                    .build(),
                Series.builder()
                    .title("Ведьмак")
                    .description("Геральт из Ривии — мутант-охотник на монстров — ищет своё место в мире, где люди и чудовища одинаково опасны.")
                    .genre("Фэнтези")
                    .year(2019)
                    .country("США")
                    .imdbRating(8.2)
                    .status(Series.Status.ONGOING)
                    .episodesCount(24)
                    .build(),
                Series.builder()
                    .title("Шерлок")
                    .description("Современная интерпретация историй о Шерлоке Холмсе. Детектив-консультант и его друг Ватсон расследуют запутанные преступления.")
                    .genre("Детектив")
                    .year(2010)
                    .country("Великобритания")
                    .imdbRating(9.1)
                    .status(Series.Status.COMPLETED)
                    .episodesCount(13)
                    .build(),
                Series.builder()
                    .title("Тёмное")
                    .description("Немецкий научно-фантастический триллер о четырёх взаимосвязанных семьях и загадочных событиях в городе Ваальдн.")
                    .genre("Триллер")
                    .year(2017)
                    .country("Германия")
                    .imdbRating(8.8)
                    .status(Series.Status.COMPLETED)
                    .episodesCount(26)
                    .build()
            );
            seriesRepository.saveAll(samples);
            log.info("Sample series created: {} records", samples.size());
        }
    }
}
