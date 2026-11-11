package com.tecnocampus.LS2.protube_back;

import com.tecnocampus.LS2.protube_back.services.VideoLoaderService;
import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;

import java.nio.file.Paths;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

class AppStartupRunnerTest {

    @Test
    void shouldLoadTheVideosWhenInitialDataIsEnabled() throws Exception {
        VideoLoaderService videoLoaderService = mock(VideoLoaderService.class);
        MockEnvironment environment = new MockEnvironment()
                .withProperty("pro_tube.store.dir", "/videos/")
                .withProperty("pro_tube.load_initial_data", "true");
        AppStartupRunner appStartupRunner = new AppStartupRunner(environment, videoLoaderService);

        appStartupRunner.run(null);

        verify(videoLoaderService).loadVideosFromFolder(Paths.get("/videos/"));
    }

    @Test
    void shouldNotLoadTheVideosWhenInitialDataIsDisabled() throws Exception {
        VideoLoaderService videoLoaderService = mock(VideoLoaderService.class);
        MockEnvironment environment = new MockEnvironment()
                .withProperty("pro_tube.store.dir", "/videos/")
                .withProperty("pro_tube.load_initial_data", "false");
        AppStartupRunner appStartupRunner = new AppStartupRunner(environment, videoLoaderService);

        appStartupRunner.run(null);

        verify(videoLoaderService, never()).loadVideosFromFolder(Paths.get("/videos/"));
    }
}
