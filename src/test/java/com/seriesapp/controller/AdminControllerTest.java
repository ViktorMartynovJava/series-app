package com.seriesapp.controller;

import com.seriesapp.service.SeriesService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.data.domain.PageImpl;

import java.util.List;


@WebMvcTest(AdminController.class)
@WithMockUser(roles = "ADMIN")
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeriesService seriesService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void dashboard() throws Exception {
        when(seriesService.findAll(null, null, 0, 100)).thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"))
                .andExpect(model().attributeExists("seriesList"));

    }

    @Test
    void newSeriesForm() {
    }

    @Test
    void createSeries() {
    }

    @Test
    void editSeriesForm() {
    }

    @Test
    void updateSeries() {
    }

    @Test
    void deleteSeries() {
    }

    @Test
    void addGenre() {
    }

    @Test
    void deleteGenre() {
    }
}