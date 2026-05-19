package com.seriesapp.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CommentDto {

    @NotBlank(message = "Комментарий не может быть пустым")
    @Size(min = 5, max = 500, message = "Длина комментария: 5-500 символов")
    private String content;

    @Min(value = 1, message = "Минимальная оценка: 1")
    @Max(value = 10, message = "Максимальная оценка: 10")
    private Integer rating;
}
