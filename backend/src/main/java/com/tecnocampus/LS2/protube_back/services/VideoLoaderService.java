package com.tecnocampus.LS2.protube_back.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecnocampus.LS2.protube_back.entity.Video;
import com.tecnocampus.LS2.protube_back.repository.VideoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
public class VideoLoaderService {

    private static final Logger LOG = LoggerFactory.getLogger(VideoLoaderService.class);

    private final VideoRepository videoRepository;
    private final ObjectMapper objectMapper;

    public VideoLoaderService(VideoRepository videoRepository) {
        this(videoRepository, new ObjectMapper());
    }

    @Autowired
    public VideoLoaderService(VideoRepository videoRepository, ObjectMapper objectMapper) {
        this.videoRepository = videoRepository;
        this.objectMapper = objectMapper;
    }

    public int loadVideosFromFolder(Path folder) {
        if (videoRepository.count() > 0) {
            return 0;
        }
        if (folder == null || !Files.isDirectory(folder)) {
            LOG.warn("Video folder does not exist: {}", folder);
            return 0;
        }

        int savedVideos = 0;
        try (DirectoryStream<Path> files = Files.newDirectoryStream(folder, "*.json")) {
            for (Path file : files) {
                if (Files.isRegularFile(file) && loadVideo(file)) {
                    savedVideos++;
                }
            }
        } catch (IOException exception) {
            LOG.warn("Could not read video folder: {}", folder, exception);
        }

        return savedVideos;
    }

    private boolean loadVideo(Path file) {
        try {
            JsonNode json = readJson(file);
            Video video = createVideo(json);
            videoRepository.save(video);
            return true;
        } catch (IOException | IllegalArgumentException exception) {
            LOG.warn("Could not load video data from: {}", file, exception);
            return false;
        }
    }

    private JsonNode readJson(Path file) throws IOException {
        JsonNode json = objectMapper.readTree(file.toFile());
        if (json == null || !json.isObject()) {
            throw new IllegalArgumentException("Invalid JSON");
        }
        return json;
    }

    private Video createVideo(JsonNode json) {
        JsonNode metadata = getObject(json, "meta");
        String fileId = getFileId(json);

        Video video = new Video();
        video.setTitle(getText(json, "title"));
        video.setDescription(getText(metadata, "description"));
        video.setAuthorName(getText(json, "user"));
        video.setCategory(getCategory(metadata));
        video.setTags(getTags(metadata));
        video.setVideoFileName(fileId + ".mp4");
        video.setThumbnailFileName(fileId + ".webp");
        video.setWidth(getPositiveNumber(json, "width"));
        video.setHeight(getPositiveNumber(json, "height"));
        video.setDuration(getPositiveDecimal(json, "duration"));
        video.setViewCount(getNonNegativeLong(metadata, "view_count"));
        video.setCreatedAt(getDate(json));
        return video;
    }

    private JsonNode getObject(JsonNode json, String field) {
        JsonNode value = json.get(field);
        if (value == null || !value.isObject()) {
            throw new IllegalArgumentException("Missing object: " + field);
        }
        return value;
    }

    private String getFileId(JsonNode json) {
        JsonNode id = json.get("id");
        if (id == null || !id.canConvertToInt() || id.asInt() < 0) {
            throw new IllegalArgumentException("Invalid id");
        }
        return String.valueOf(id.asInt());
    }

    private String getText(JsonNode json, String field) {
        JsonNode value = json.get(field);
        if (value == null || !value.isTextual() || value.asText().isBlank()) {
            throw new IllegalArgumentException("Missing text: " + field);
        }
        return value.asText();
    }

    private int getPositiveNumber(JsonNode json, String field) {
        JsonNode value = json.get(field);
        if (value == null || !value.canConvertToInt() || value.asInt() <= 0) {
            throw new IllegalArgumentException("Invalid number: " + field);
        }
        return value.asInt();
    }

    private double getPositiveDecimal(JsonNode json, String field) {
        JsonNode value = json.get(field);
        if (value == null || !value.isNumber() || value.asDouble() <= 0
                || !Double.isFinite(value.asDouble())) {
            throw new IllegalArgumentException("Invalid decimal: " + field);
        }
        return value.asDouble();
    }

    private long getNonNegativeLong(JsonNode json, String field) {
        JsonNode value = json.get(field);
        if (value == null || !value.canConvertToLong() || value.asLong() < 0) {
            throw new IllegalArgumentException("Invalid number: " + field);
        }
        return value.asLong();
    }

    private String getCategory(JsonNode metadata) {
        JsonNode categories = metadata.get("categories");
        if (categories == null || !categories.isArray() || categories.isEmpty()) {
            throw new IllegalArgumentException("Missing categories");
        }
        JsonNode category = categories.get(0);
        if (!category.isTextual() || category.asText().isBlank()) {
            throw new IllegalArgumentException("Invalid category");
        }
        return category.asText();
    }

    private List<String> getTags(JsonNode metadata) {
        JsonNode jsonTags = metadata.get("tags");
        if (jsonTags == null || !jsonTags.isArray()) {
            throw new IllegalArgumentException("Missing tags");
        }

        List<String> tags = new ArrayList<>();
        for (JsonNode tag : jsonTags) {
            if (tag.isTextual() && !tag.asText().isBlank()) {
                tags.add(tag.asText());
            }
        }
        return tags;
    }

    private LocalDateTime getDate(JsonNode json) {
        JsonNode timestamp = json.get("timestamp");
        if (timestamp == null || !timestamp.canConvertToLong() || timestamp.asLong() < 0) {
            throw new IllegalArgumentException("Invalid timestamp");
        }
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp.asLong()), ZoneOffset.UTC);
    }
}
