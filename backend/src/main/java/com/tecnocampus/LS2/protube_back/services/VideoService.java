package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.dto.VideoDetailResponse;
import com.tecnocampus.LS2.protube_back.dto.VideoResponse;
import com.tecnocampus.LS2.protube_back.entity.Video;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VideoService {

    private static final int MAX_PAGE_SIZE = 100;

    private final VideoRepository videoRepository;

    public VideoService(VideoRepository videoRepository) {
        this.videoRepository = videoRepository;
    }

    public List<VideoResponse> getVideos(int page, int size) {
        validatePage(page, size);

        Pageable pageRequest = PageRequest.of(page, size, Sort.by("id"));
        Page<Video> videoPage = videoRepository.findAll(pageRequest);
        List<VideoResponse> videos = new ArrayList<>();

        for (Video video : videoPage.getContent()) {
            String thumbnailUrl = "/media/" + video.getThumbnailFileName();
            VideoResponse response = new VideoResponse(
                    video.getId(), video.getTitle(), thumbnailUrl, video.getDuration());
            videos.add(response);
        }

        return videos;
    }

    public VideoDetailResponse getVideoById(Long id) {
        Optional<Video> foundVideo = videoRepository.findById(id);
        if (foundVideo.isEmpty()) {
            throw new VideoNotFoundException(id);
        }

        Video video = foundVideo.get();
        String videoUrl = "/media/" + video.getVideoFileName();
        String thumbnailUrl = "/media/" + video.getThumbnailFileName();

        return new VideoDetailResponse(
                video.getId(),
                video.getTitle(),
                video.getDescription(),
                video.getAuthorName(),
                video.getTags(),
                videoUrl,
                thumbnailUrl,
                video.getDuration());
    }

    private void validatePage(int page, int size) {
        if (page < 0) {
            throw new InvalidPaginationException("Page cannot be negative");
        }
        if (size <= 0) {
            throw new InvalidPaginationException("Size must be greater than zero");
        }
        if (size > MAX_PAGE_SIZE) {
            throw new InvalidPaginationException("Size cannot be greater than " + MAX_PAGE_SIZE);
        }
    }
}
