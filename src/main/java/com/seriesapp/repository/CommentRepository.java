package com.seriesapp.repository;

import com.seriesapp.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    boolean existsByUserIdAndSeriesId(Long userId, Long seriesId);

    @Query("SELECT c FROM Comment c WHERE c.series.id = :seriesId " +
            "ORDER BY CASE WHEN c.user.role = com.seriesapp.entity.User$Role.ADMIN THEN 0 ELSE 1 END ASC, " +
            "c.createdAt DESC")
    Page<Comment> findBySeriesId(@Param("seriesId") Long seriesId, Pageable pageable);

    /** Принудительно делаем запрос на сортировку коментария Админа на первое место в списке **/
}
