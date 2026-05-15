package com.seriesapp.service;

import com.seriesapp.dto.CommentDto;
import com.seriesapp.entity.Comment;
import com.seriesapp.entity.Series;
import com.seriesapp.entity.User;
import com.seriesapp.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Page<Comment> findBySeries(Long seriesId, int page) {
        return commentRepository.findBySeriesId(seriesId,
                PageRequest.of(page, 10, Sort.by("createdAt").descending()));
    }

    @Transactional
    public Comment addComment(CommentDto dto, User user, Series series) {
        Comment comment = Comment.builder()
                .content(dto.getContent())
                .rating(dto.getRating())
                .user(user)
                .series(series)
                .build();
        return commentRepository.save(comment);
    }

    @Transactional
    public void delete(Long commentId, User currentUser) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Комментарий не найден"));

        boolean isOwner = comment.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == User.Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new SecurityException("Нет доступа для удаления комментария");
        }
        commentRepository.delete(comment);
    }
}
