package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.dto.VideoDetailResponse;
import com.tecnocampus.LS2.protube_back.services.VideoNotFoundException;
import com.tecnocampus.LS2.protube_back.services.VideoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Calls the real HTTP endpoint (without starting the server) to check the URL and the status codes
@WebMvcTest(controllers = VideosController.class, properties = "pro_tube.store.dir=/tmp/")
class VideosControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VideoService videoService;

    @Test
    void shouldReturnTheVideoDetailsWithStatus200() throws Exception {
        VideoDetailResponse details = new VideoDetailResponse(1L, "Relaxing piano music", "One minute of piano.",
                "Music Channel", List.of("piano", "relax"), "/media/1.mp4", "/media/1.webp", 65.0);
        when(videoService.getVideoById(1L)).thenReturn(details);

        mockMvc.perform(get("/api/videos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Relaxing piano music"))
                .andExpect(jsonPath("$.author").value("Music Channel"))
                .andExpect(jsonPath("$.videoUrl").value("/media/1.mp4"));
    }

    @Test
    void shouldReturnStatus404WhenTheVideoDoesNotExist() throws Exception {
        when(videoService.getVideoById(99L)).thenThrow(new VideoNotFoundException(99L));

        mockMvc.perform(get("/api/videos/99"))
                .andExpect(status().isNotFound());
    }
}
