package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.entity.Video;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
@ActiveProfiles("test")
class VideoLoaderServiceTest {

    // Two .json files (0.json and 1.json) and one .txt file that must be ignored
    private static final Path TEST_STORE_FOLDER = Paths.get("src/test/resources/store");

    @Autowired
    private VideoRepository videoRepository;

    private VideoLoaderService videoLoaderService;

    @BeforeEach
    void setUp() {
        videoLoaderService = new VideoLoaderService(videoRepository);
    }

    @Test
    void shouldSaveOneVideoForEachJsonFile() throws Exception {
        int savedVideos = videoLoaderService.loadVideosFromFolder(TEST_STORE_FOLDER);

        assertEquals(2, savedVideos);
        assertEquals(2, videoRepository.count());
    }

    @Test
    void shouldCopyTheDataOfTheJsonFileIntoTheVideo() throws Exception {
        videoLoaderService.loadVideosFromFolder(TEST_STORE_FOLDER);

        Video video = findVideoByTitle("Learning Java in 30 seconds");
        assertEquals("A very short Java lesson.", video.getDescription());
        assertEquals("Education", video.getCategory());
        assertEquals(List.of("java", "programming"), video.getTags());
        assertEquals(1280, video.getWidth());
        assertEquals(720, video.getHeight());
        assertEquals(30.5, video.getDuration());
        assertEquals(1500, video.getViewCount());
        assertNotNull(video.getCreatedAt());
    }

    @Test
    void shouldUseTheIdOfTheJsonFileForTheMediaFileNames() throws Exception {
        videoLoaderService.loadVideosFromFolder(TEST_STORE_FOLDER);

        Video video = findVideoByTitle("Relaxing piano music");
        assertEquals("1.mp4", video.getVideoFileName());
        assertEquals("1.webp", video.getThumbnailFileName());
    }

    @Test
    void shouldNotLoadAnythingWhenTheDatabaseAlreadyHasVideos() throws Exception {
        videoLoaderService.loadVideosFromFolder(TEST_STORE_FOLDER);

        int savedVideosSecondTime = videoLoaderService.loadVideosFromFolder(TEST_STORE_FOLDER);

        assertEquals(0, savedVideosSecondTime);
        assertEquals(2, videoRepository.count());
    }

    private Video findVideoByTitle(String title) {
        List<Video> allVideos = videoRepository.findAll();
        for (Video video : allVideos) {
            if (video.getTitle().equals(title)) {
                return video;
            }
        }
        throw new AssertionError("No video with title: " + title);
    }
}
