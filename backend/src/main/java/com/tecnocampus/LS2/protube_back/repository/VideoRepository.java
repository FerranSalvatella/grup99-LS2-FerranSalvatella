package com.tecnocampus.LS2.protube_back.repository;

import com.tecnocampus.LS2.protube_back.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoRepository extends JpaRepository<Video, Long> {
}
