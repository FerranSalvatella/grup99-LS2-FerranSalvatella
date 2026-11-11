package com.tecnocampus.LS2.protube_back.dto;

public class VideoResponse {

    private final Long id;
    private final String title;
    private final String thumbnailUrl;
    private final double duration;

    public VideoResponse(Long id, String title, String thumbnailUrl, double duration) {
        this.id = id;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.duration = duration;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public double getDuration() {
        return duration;
    }
}
