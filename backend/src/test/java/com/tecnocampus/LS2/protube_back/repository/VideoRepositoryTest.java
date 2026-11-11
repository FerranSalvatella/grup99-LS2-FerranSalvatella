package com.tecnocampus.LS2.protube_back.repository;

import com.tecnocampus.LS2.protube_back.entity.Video;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class VideoRepositoryTest {

    @Autowired
    private VideoRepository videoRepository;

    @Test
    void shouldSaveVideoAndGiveItAnId() {
        Video video = createVideo("My first video");

        Video savedVideo = videoRepository.save(video);

        assertNotNull(savedVideo.getId());
    }

    @Test
    void shouldFindSavedVideoById() {
        Video savedVideo = videoRepository.save(createVideo("Cooking pasta"));

        Optional<Video> foundVideo = videoRepository.findById(savedVideo.getId());

        assertTrue(foundVideo.isPresent());
        assertEquals("Cooking pasta", foundVideo.get().getTitle());
        assertEquals(List.of("food", "italian"), foundVideo.get().getTags());
    }

    @Test
    void shouldReturnEmptyWhenVideoDoesNotExist() {
        long videoIdThatDoesNotExist = 999L;

        Optional<Video> foundVideo = videoRepository.findById(videoIdThatDoesNotExist);

        assertTrue(foundVideo.isEmpty());
    }

    private Video createVideo(String title) {
        Video video = new Video();
        video.setTitle(title);
        video.setDescription("A short description");
        video.setCategory("Food");
        video.setTags(List.of("food", "italian"));
        video.setVideoFileName("1.mp4");
        video.setThumbnailFileName("1.webp");
        video.setWidth(1280);
        video.setHeight(720);
        video.setDuration(30.5);
        video.setViewCount(0);
        video.setCreatedAt(LocalDateTime.now());
        return video;
    }
}
