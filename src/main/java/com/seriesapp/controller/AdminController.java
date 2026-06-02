package com.seriesapp.controller;

import com.seriesapp.dto.SeriesDto;
import com.seriesapp.entity.Genre;
import com.seriesapp.entity.Series;
import com.seriesapp.service.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final SeriesService seriesService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("seriesList", seriesService.findAll(null, null, 0, 100).getContent());
        return "admin/dashboard";
    }

    @GetMapping("/series/new")
    public String newSeriesForm(Model model) {
        model.addAttribute("seriesDto", new SeriesDto());
        model.addAttribute("statuses", Series.Status.values());
        model.addAttribute("availableGenres", seriesService.getAllAvailableGenreEntities());
        return "admin/series-form";
    }

    @PostMapping("/series/new")
    public String createSeries(@Valid @ModelAttribute SeriesDto seriesDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", Series.Status.values());
            model.addAttribute("availableGenres", seriesService.getAllAvailableGenreEntities());
            return "admin/series-form";
        }
        seriesService.save(seriesDto);
        redirectAttributes.addFlashAttribute("successMessage", "Сериал добавлен!");
        return "redirect:/admin";
    }

    @GetMapping("/series/{id}/edit")
    public String editSeriesForm(@PathVariable Long id, Model model) {
        Series series = seriesService.findById(id);
        SeriesDto dto = new SeriesDto();
        dto.setTitle(series.getTitle());
        dto.setDescription(series.getDescription());
        dto.setPosterUrl(series.getPosterUrl());
        dto.setTrailerUrl(series.getTrailerUrl());
        dto.setVideoUrl(series.getVideoUrl());
        if (series.getGenre() != null && !series.getGenre().isBlank()) {
            dto.setGenres(Arrays.asList(series.getGenre().split(",\\s*")));
        }
        dto.setYear(series.getYear());
        dto.setCountry(series.getCountry());
        dto.setImdbRating(series.getImdbRating());
        dto.setStatus(series.getStatus());
        dto.setEpisodesCount(series.getEpisodesCount());
        model.addAttribute("seriesDto", dto);
        model.addAttribute("seriesId", id);
        model.addAttribute("statuses", Series.Status.values());
        model.addAttribute("availableGenres", seriesService.getAllAvailableGenreEntities());
        return "admin/series-form";
    }

    @PostMapping("/series/{id}/edit")
    public String updateSeries(@PathVariable Long id,
                               @Valid @ModelAttribute SeriesDto seriesDto,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("statuses", Series.Status.values());
            model.addAttribute("seriesId", id);
            model.addAttribute("availableGenres", seriesService.getAllAvailableGenreEntities());
            return "admin/series-form";
        }
        seriesService.update(id, seriesDto);
        redirectAttributes.addFlashAttribute("successMessage", "Сериал обновлён!");
        return "redirect:/admin";
    }

    @PostMapping("/series/{id}/delete")
    public String deleteSeries(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        seriesService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Сериал удалён!");
        return "redirect:/admin";
    }

    @PostMapping("/genres")
    @ResponseBody
    public ResponseEntity<?> addGenre(@RequestBody Map<String, String> body) {
        String name = body.get("name");
        if (name == null || name.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Название не может быть пустым"));
        }
        try {
            Genre genre = seriesService.addGenre(name.trim());
            return ResponseEntity.ok(Map.of("id", genre.getId(), "name", genre.getName()));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }


    @DeleteMapping("/genres/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteGenre(@PathVariable Long id) {
        try {
            seriesService.deleteGenre(id);
            return ResponseEntity.ok(Map.of("deleted", true));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}