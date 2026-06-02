package com.seriesapp.service;

import com.seriesapp.dto.SeriesDto;
import com.seriesapp.entity.Genre;
import com.seriesapp.entity.Series;
import com.seriesapp.repository.GenreRepository;
import com.seriesapp.repository.SeriesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeriesService {

    private final SeriesRepository seriesRepository;
    private final GenreRepository genreRepository;


    public Page<Series> findAll(String search, String genre, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        boolean hasSearch = search != null && !search.isBlank();
        boolean hasGenre  = genre  != null && !genre.isBlank();

        if (hasSearch && hasGenre) {
            return seriesRepository.findByTitleContainingIgnoreCaseAndGenreContainingIgnoreCase(search, genre, pageable);
        } else if (hasSearch) {
            return seriesRepository.findByTitleContainingIgnoreCase(search, pageable);
        } else if (hasGenre) {
            return seriesRepository.findByGenreContainingIgnoreCase(genre, pageable);
        }
        return seriesRepository.findAll(pageable);
    }

    public Series findById(Long id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Сериал не найден: " + id));
    }

    public List<Series> findTopRated(int limit) {
        return seriesRepository.findTopRated(PageRequest.of(0, limit));
    }

    public List<Series> findLatest(int limit) {
        return seriesRepository.findLatest(PageRequest.of(0, limit));
    }

    @Transactional
    public Series save(SeriesDto dto) {
        Series series = new Series();
        mapDtoToSeries(dto, series);
        return seriesRepository.save(series);
    }

    @Transactional
    public Series update(Long id, SeriesDto dto) {
        Series series = findById(id);
        mapDtoToSeries(dto, series);
        return seriesRepository.save(series);
    }

    @Transactional
    public void delete(Long id) {
        seriesRepository.deleteById(id);
    }

    @Transactional
    public void updateAudio(Long id, String audioUrl, String audioTitle) {
        Series series = findById(id);
        series.setAdminAudioUrl(audioUrl);
        series.setAdminAudioTitle(audioTitle);
        seriesRepository.save(series);
    }

    private void mapDtoToSeries(SeriesDto dto, Series series) {
        series.setTitle(dto.getTitle());
        series.setDescription(dto.getDescription());
        series.setPosterUrl(dto.getPosterUrl());
        series.setTrailerUrl(dto.getTrailerUrl());
        series.setVideoUrl(dto.getVideoUrl());
        series.setGenre(
                (dto.getGenres() != null && !dto.getGenres().isEmpty())
                        ? String.join(", ", dto.getGenres())
                        : ""
        );
        series.setYear(dto.getYear());
        series.setCountry(dto.getCountry());
        series.setImdbRating(dto.getImdbRating());
        series.setStatus(dto.getStatus() != null ? dto.getStatus() : Series.Status.ONGOING);
        series.setEpisodesCount(dto.getEpisodesCount());
    }

    public List<String> getAllAvailableGenres() {
        return genreRepository.findAll()
                .stream()
                .map(Genre::getName)
                .toList();
    }

    @Transactional
    public Genre addGenre(String genreName) {
        if (genreName == null || genreName.isBlank()) {
            throw new IllegalArgumentException("Название жанра не может быть пустым");
        }
        if (genreRepository.existsByNameIgnoreCase(genreName.trim())) {
            throw new RuntimeException("Жанр уже существует: " + genreName);
        }
        Genre genre = new Genre();
        genre.setName(genreName.trim());
        return genreRepository.save(genre);
    }


    @Transactional
    public void deleteGenre(Long id) {
        if (!genreRepository.existsById(id)) {
            throw new RuntimeException("Жанр не найден: " + id);
        }
        genreRepository.deleteById(id);
    }

    public List<String> findAllGenres() {
        return seriesRepository.findAllGenres();
    }

    public List<Genre> getAllAvailableGenreEntities() {
        return genreRepository.findAll();
    }
}