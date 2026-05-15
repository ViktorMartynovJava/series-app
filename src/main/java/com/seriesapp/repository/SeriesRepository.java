package com.seriesapp.repository;

import com.seriesapp.entity.Series;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {

    Page<Series> findByTitleContainingIgnoreCase(String title, Pageable pageable);

    Page<Series> findByGenreIgnoreCase(String genre, Pageable pageable);

    Page<Series> findByTitleContainingIgnoreCaseAndGenreIgnoreCase(
            String title, String genre, Pageable pageable);

    @Query("SELECT DISTINCT s.genre FROM Series s WHERE s.genre IS NOT NULL ORDER BY s.genre")
    List<String> findAllGenres();

    @Query("SELECT s FROM Series s LEFT JOIN s.comments c GROUP BY s ORDER BY AVG(COALESCE(c.rating, 0)) DESC")
    List<Series> findTopRated(Pageable pageable);

    @Query("SELECT s FROM Series s ORDER BY s.createdAt DESC")
    List<Series> findLatest(Pageable pageable);

    @Query("SELECT s FROM Series s WHERE s.id IN " +
           "(SELECT f.id FROM User u JOIN u.favorites f WHERE u.id = :userId)")
    List<Series> findFavoritesByUserId(@Param("userId") Long userId);
}
