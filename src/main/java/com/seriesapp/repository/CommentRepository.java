package com.seriesapp.repository;

import com.seriesapp.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findBySeriesId(Long seriesId, Pageable pageable);
    boolean existsByUserIdAndSeriesId(Long userId, Long seriesId);
}
