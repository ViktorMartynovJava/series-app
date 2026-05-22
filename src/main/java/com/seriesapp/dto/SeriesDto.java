package com.seriesapp.dto;

import com.seriesapp.entity.Series;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class SeriesDto {

    @NotBlank(message = "Название обязательно")
    private String title;

    private String description;
    private String posterUrl;
    private String trailerUrl;
    private String videoUrl;
    private List<String> genres;

    @Min(1900) @Max(2100)
    private Integer year;

    private String country;

    @DecimalMin("0.0") @DecimalMax("10.0")
    private Double imdbRating;

    private Series.Status status;
    private Integer episodesCount;
}
