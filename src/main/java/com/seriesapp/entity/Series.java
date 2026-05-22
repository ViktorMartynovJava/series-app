package com.seriesapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "series")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "poster_url")
    private String posterUrl;

    @Column(name = "trailer_url")
    private String trailerUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "genre")
    private String genre;

    private Integer year;

    private String country;

    @Column(name = "imdb_rating")
    private Double imdbRating;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ONGOING;

    @Column(name = "episodes_count")
    private Integer episodesCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "series", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt DESC")
    private List<Comment> comments = new ArrayList<>();

    @ManyToMany(mappedBy = "favorites")
    private List<User> favoritedBy = new ArrayList<>();

    public enum Status {
        ONGOING, COMPLETED, CANCELLED, ANNOUNCED
    }

    public double getAverageRating() {
        return comments.stream()
                .filter(c -> c.getRating() != null)
                .mapToInt(Comment::getRating)
                .average()
                .orElse(0.0);
    }
}
