package com.tecnocampus.LS2.protube_back.dto;

import java.util.List;

public class VideoDetailResponse {

    private final Long id;
    private final String title;
    private final String description;
    private final String author;
    private final List<String> tags;
    private final String videoUrl;
    private final String thumbnailUrl;
    private final double duration;

    public VideoDetailResponse(Long id, String title, String description, String author,
                               List<String> tags, String videoUrl, String thumbnailUrl, double duration) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.author = author;
        this.tags = tags;
        this.videoUrl = videoUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return author;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public double getDuration() {
        return duration;
    }
}
