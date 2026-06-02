package com.seriesapp.controller;


import com.seriesapp.entity.Series;
import com.seriesapp.service.SeriesService;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class VideoController {


    private final SeriesService seriesService;

    public VideoController(SeriesService seriesService) {
        this.seriesService = seriesService;
    }

    @GetMapping("/api/videos/trailer/{id}")
    public ResponseEntity<ResourceRegion> getTrailer(
            @PathVariable Long id,
            @RequestHeader HttpHeaders headers) throws IOException {

        Series series = seriesService.findById(id);

        if (series == null || series.getTrailerUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        String fileName = series.getTrailerUrl();

        Resource video = new ClassPathResource("static/videos/" + fileName);

        if (!video.exists()) {
            return ResponseEntity.notFound().build();
        }

        ResourceRegion region = resourceRegion(video, headers);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(region);
    }

    private ResourceRegion resourceRegion(Resource video, HttpHeaders headers) throws IOException {
        long contentLength = video.contentLength();
        HttpRange range = headers.getRange().isEmpty() ? null : headers.getRange().get(0);

        if (range != null) {
            long start = range.getRangeStart(contentLength);
            long end = range.getRangeEnd(contentLength);

            long rangeLength = Math.min(1 * 1024 * 1024, end - start + 1);
            return new ResourceRegion(video, start, rangeLength);
        } else {
            long rangeLength = Math.min(1 * 1024 * 1024, contentLength);
            return new ResourceRegion(video, 0, rangeLength);
        }
    }
}
