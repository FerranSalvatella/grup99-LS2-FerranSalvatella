package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.dto.VideoDetailResponse;
import com.tecnocampus.LS2.protube_back.dto.VideoResponse;
import com.tecnocampus.LS2.protube_back.entity.Video;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VideoServiceTest {

    @Mock
    private VideoRepository videoRepository;

    @InjectMocks
    private VideoService videoService;

    @Test
    void shouldTurnEachVideoIntoAVideoResponse() {
        Video video = new Video();
        video.setTitle("Relaxing piano music");
        video.setThumbnailFileName("1.webp");
        video.setDuration(65.0);
        Pageable firstPage = PageRequest.of(0, 12, Sort.by("id"));
        when(videoRepository.findAll(firstPage)).thenReturn(new PageImpl<>(List.of(video)));

        List<VideoResponse> videos = videoService.getVideos(0, 12);

        assertEquals(1, videos.size());
        assertEquals("Relaxing piano music", videos.get(0).getTitle());
        assertEquals("/media/1.webp", videos.get(0).getThumbnailUrl());
        assertEquals(65.0, videos.get(0).getDuration());
    }

    @Test
    void shouldAskTheRepositoryForTheRequestedPage() {
        Pageable thirdPageOfFive = PageRequest.of(2, 5, Sort.by("id"));
        when(videoRepository.findAll(thirdPageOfFive)).thenReturn(new PageImpl<>(List.of()));

        List<VideoResponse> videos = videoService.getVideos(2, 5);

        assertTrue(videos.isEmpty());
    }

    @Test
    void shouldReturnAllTheDetailsOfOneVideo() {
        Video video = new Video();
        video.setTitle("Relaxing piano music");
        video.setDescription("One minute of piano.");
        video.setAuthorName("Music Channel");
        video.setTags(List.of("piano", "relax"));
        video.setVideoFileName("1.mp4");
        video.setThumbnailFileName("1.webp");
        video.setDuration(65.0);
        when(videoRepository.findById(5L)).thenReturn(Optional.of(video));

        VideoDetailResponse details = videoService.getVideoById(5L);

        assertEquals("Relaxing piano music", details.getTitle());
        assertEquals("One minute of piano.", details.getDescription());
        assertEquals("Music Channel", details.getAuthor());
        assertEquals(List.of("piano", "relax"), details.getTags());
        assertEquals("/media/1.mp4", details.getVideoUrl());
        assertEquals("/media/1.webp", details.getThumbnailUrl());
        assertEquals(65.0, details.getDuration());
    }

    @Test
    void shouldThrowVideoNotFoundWhenTheIdDoesNotExist() {
        when(videoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(VideoNotFoundException.class, () -> videoService.getVideoById(99L));
    }

    @Test
    void shouldRejectANegativePage() {
        assertThrows(InvalidPaginationException.class, () -> videoService.getVideos(-1, 12));
    }

    @Test
    void shouldRejectAPageSizeOfZero() {
        assertThrows(InvalidPaginationException.class, () -> videoService.getVideos(0, 0));
    }

    @Test
    void shouldRejectAPageSizeBiggerThanTheMaximum() {
        assertThrows(InvalidPaginationException.class, () -> videoService.getVideos(0, 101));
    }
}
