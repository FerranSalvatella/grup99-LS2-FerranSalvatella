package com.tecnocampus.LS2.protube_back.configuration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Checks the security rules with the whole application (without a real server):
// which URLs are public, which ones need login, CORS and the password encoder.
@SpringBootTest({
        "pro_tube.store.dir=/tmp/",
        "pro_tube.load_initial_data=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SecurityConfigTest {

    private static final int UNAUTHORIZED = 401;
    private static final int FORBIDDEN = 403;
    private static final String FRONTEND_ORIGIN = "http://localhost:5173";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // ----- Public URLs: they work without login -----

    @Test
    void videoListIsPublic() throws Exception {
        mockMvc.perform(get("/api/videos"))
                .andExpect(status().isOk());
    }

    @Test
    void videoDetailIsPublic() throws Exception {
        // The video does not exist: we get 404 from the controller, not 401 from the security
        mockMvc.perform(get("/api/videos/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void mediaFilesArePublic() throws Exception {
        MvcResult result = mockMvc.perform(get("/media/0.webp")).andReturn();

        assertNotBlockedBySecurity(result);
    }

    @Test
    void registerIsPublic() throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/register").with(csrf())).andReturn();

        assertNotBlockedBySecurity(result);
    }

    @Test
    void frontendFilesArePublic() throws Exception {
        // The built React app is served as static files (index.html, /assets/...)
        MvcResult result = mockMvc.perform(get("/assets/index.js")).andReturn();

        assertNotBlockedBySecurity(result);
    }

    // ----- Private URLs: without login the answer is 401 -----

    @Test
    void analyticsNeedsLogin() throws Exception {
        mockMvc.perform(get("/api/users/me/analytics"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void uploadingAVideoNeedsLogin() throws Exception {
        mockMvc.perform(post("/api/videos").with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // ----- CORS: the frontend (another port) can call the API -----

    @Test
    void frontendCanAskForTheVideoList() throws Exception {
        mockMvc.perform(options("/api/videos")
                        .header("Origin", FRONTEND_ORIGIN)
                        .header("Access-Control-Request-Method", "GET"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN));
    }

    @Test
    void frontendCanSendTheSessionCookie() throws Exception {
        mockMvc.perform(options("/api/auth/login")
                        .header("Origin", FRONTEND_ORIGIN)
                        .header("Access-Control-Request-Method", "POST"))
                .andExpect(status().isOk())
                .andExpect(header().string("Access-Control-Allow-Origin", FRONTEND_ORIGIN))
                .andExpect(header().string("Access-Control-Allow-Credentials", "true"));
    }

    // ----- Passwords are saved with BCrypt -----

    @Test
    void passwordEncoderDoesNotKeepThePlainPassword() {
        String hash = passwordEncoder.encode("secret123");

        assertNotEquals("secret123", hash);
    }

    @Test
    void passwordEncoderUsesBCrypt() {
        String hash = passwordEncoder.encode("secret123");

        // Every BCrypt hash starts with "$2"
        assertTrue(hash.startsWith("$2"));
    }

    @Test
    void passwordEncoderRecognisesTheRightPassword() {
        String hash = passwordEncoder.encode("secret123");

        assertTrue(passwordEncoder.matches("secret123", hash));
        assertFalse(passwordEncoder.matches("wrong-password", hash));
    }

    private void assertNotBlockedBySecurity(MvcResult result) {
        int statusCode = result.getResponse().getStatus();
        assertNotEquals(UNAUTHORIZED, statusCode);
        assertNotEquals(FORBIDDEN, statusCode);
    }
}
