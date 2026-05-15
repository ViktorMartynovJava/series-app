package com.seriesapp.controller;

import com.seriesapp.dto.CommentDto;
import com.seriesapp.entity.Series;
import com.seriesapp.entity.User;
import com.seriesapp.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class SeriesController {

    private final SeriesService seriesService;
    private final CommentService commentService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("topRated", seriesService.findTopRated(6));
        model.addAttribute("latest", seriesService.findLatest(6));
        return "series/home";
    }

    @GetMapping("/series")
    public String catalog(@RequestParam(defaultValue = "") String search,
                          @RequestParam(defaultValue = "") String genre,
                          @RequestParam(defaultValue = "0") int page,
                          Model model) {
        Page<Series> seriesPage = seriesService.findAll(search, genre, page, 12);
        model.addAttribute("seriesPage", seriesPage);
        model.addAttribute("genres", seriesService.findAllGenres());
        model.addAttribute("search", search);
        model.addAttribute("selectedGenre", genre);
        return "series/catalog";
    }

    @GetMapping("/series/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(defaultValue = "0") int commentPage,
                         @AuthenticationPrincipal User currentUser,
                         Model model) {
        Series series = seriesService.findById(id);
        model.addAttribute("series", series);
        model.addAttribute("comments", commentService.findBySeries(id, commentPage));
        model.addAttribute("commentDto", new CommentDto());

        if (currentUser != null) {
            User user = userService.findByUsername(currentUser.getUsername());
            model.addAttribute("isFavorite", user.getFavorites().contains(series));
        }
        return "series/detail";
    }

    @PostMapping("/series/{id}/comment")
    @PreAuthorize("isAuthenticated()")
    public String addComment(@PathVariable Long id,
                             @Valid @ModelAttribute CommentDto commentDto,
                             BindingResult result,
                             @AuthenticationPrincipal User currentUser,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("commentError",
                    result.getAllErrors().get(0).getDefaultMessage());
            return "redirect:/series/" + id;
        }
        User user = userService.findByUsername(currentUser.getUsername());
        Series series = seriesService.findById(id);
        commentService.addComment(commentDto, user, series);
        return "redirect:/series/" + id;
    }

    @PostMapping("/series/{id}/favorite")
    @PreAuthorize("isAuthenticated()")
    public String toggleFavorite(@PathVariable Long id,
                                 @AuthenticationPrincipal User currentUser) {
        User user = userService.findByUsername(currentUser.getUsername());
        Series series = seriesService.findById(id);
        userService.toggleFavorite(user.getId(), series);
        return "redirect:/series/" + id;
    }

    @PostMapping("/comment/{commentId}/delete")
    @PreAuthorize("isAuthenticated()")
    public String deleteComment(@PathVariable Long commentId,
                                @RequestParam Long seriesId,
                                @AuthenticationPrincipal User currentUser) {
        User user = userService.findByUsername(currentUser.getUsername());
        commentService.delete(commentId, user);
        return "redirect:/series/" + seriesId;
    }
}
