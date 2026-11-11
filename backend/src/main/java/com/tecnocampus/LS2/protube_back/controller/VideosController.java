package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.dto.VideoResponse;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/videos")
public class VideosController {

    private final VideoService videoService;

    public VideosController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping
    public ResponseEntity<List<VideoResponse>> getVideos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(videoService.getVideos(page, size));
    }
}
