package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.dto.VideoResponse;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideosControllerTest {

    @Mock
    private VideoService videoService;

    @InjectMocks
    private VideosController videosController;

    @Test
    void shouldReturnTheVideosOfTheRequestedPage() {
        VideoResponse video = new VideoResponse(1L, "Relaxing piano music", "/media/1.webp", 65.0);
        when(videoService.getVideos(0, 12)).thenReturn(List.of(video));

        ResponseEntity<List<VideoResponse>> response = videosController.getVideos(0, 12);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(List.of(video), response.getBody());
    }
}
