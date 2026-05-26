package com.seriesapp.controller;

import com.seriesapp.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAudioController {

    private final SeriesService seriesService;

    @PostMapping("/series/{id}/audio")
    public String uploadAudio(@PathVariable Long id,
                              @RequestParam("file") MultipartFile file,
                              RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Файл не выбран");
            return "redirect:/admin/series/" + id + "/edit";
        }

        try {
            String filename = "series_" + id + "_" + System.currentTimeMillis() + "_"
                    + StringUtils.cleanPath(file.getOriginalFilename());
            Path uploadDir = Paths.get("uploads/audio");
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), uploadDir.resolve(filename),
                    StandardCopyOption.REPLACE_EXISTING);

            seriesService.updateAudio(id, "/uploads/audio/" + filename, "Мое Личное Мнение");
            redirectAttributes.addFlashAttribute("success", "Аудио загружено");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Ошибка загрузки: " + e.getMessage());
        }

        return "redirect:/admin/series/" + id + "/edit";
    }

    @PostMapping("/series/{id}/audio/delete")
    public String deleteAudio(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        seriesService.updateAudio(id, null, null);
        redirectAttributes.addFlashAttribute("success", "Аудио удалено");
        return "redirect:/admin/series/" + id + "/edit";
    }
}