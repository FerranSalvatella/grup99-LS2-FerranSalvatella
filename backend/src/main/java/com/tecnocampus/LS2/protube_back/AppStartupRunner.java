package com.tecnocampus.LS2.protube_back;

import com.tecnocampus.LS2.protube_back.services.VideoLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class AppStartupRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(AppStartupRunner.class);

    private final VideoLoaderService videoLoaderService;
    private final Path storeFolder;
    private final boolean loadInitialData;

    public AppStartupRunner(Environment environment, VideoLoaderService videoLoaderService) {
        this.videoLoaderService = videoLoaderService;
        this.storeFolder = Paths.get(environment.getProperty("pro_tube.store.dir"));
        this.loadInitialData = Boolean.TRUE.equals(
                environment.getProperty("pro_tube.load_initial_data", Boolean.class));
    }

    @Override
    public void run(ApplicationArguments args) {
        if (loadInitialData) {
            int loadedVideos = videoLoaderService.loadVideosFromFolder(storeFolder);
            LOG.info("Loaded {} videos from {}", loadedVideos, storeFolder);
        }
    }
}
