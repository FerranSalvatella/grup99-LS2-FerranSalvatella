package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.entity.User;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Login, "who am I" and logout with the whole application and the real session (without a real server).
// The session returned by the login is sent again in the next requests, like the browser does with the cookie.
@SpringBootTest({
        "pro_tube.store.dir=/tmp/",
        "pro_tube.load_initial_data=false"
})
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSessionTest {

    private static final String LOGIN_URL = "/api/auth/login";
    private static final String LOGOUT_URL = "/api/auth/logout";
    private static final String ME_URL = "/api/auth/me";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void createAnna() {
        userRepository.deleteAll();
        User anna = new User();
        anna.setUsername("anna");
        anna.setEmail("anna@example.com");
        anna.setPasswordHash(passwordEncoder.encode("secret123"));
        userRepository.save(anna);
    }

    // ----- Right email and password: logged in -----

    @Test
    void loginWithTheRightPasswordAnswers200AndTheUser() throws Exception {
        mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("anna@example.com", "secret123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anna"))
                .andExpect(jsonPath("$.email").value("anna@example.com"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void afterLoginTheSessionKnowsWhoIAm() throws Exception {
        MockHttpSession session = logInAsAnna();

        mockMvc.perform(get(ME_URL).session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("anna"))
                .andExpect(jsonPath("$.email").value("anna@example.com"));
    }

    // ----- Wrong data: error and not logged in -----

    @Test
    void loginWithAWrongPasswordAnswers401() throws Exception {
        mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("anna@example.com", "wrong-password1")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginWithAWrongPasswordDoesNotCreateASession() throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("anna@example.com", "wrong-password1")))
                .andReturn();

        assertNull(result.getRequest().getSession(false));
    }

    @Test
    void loginWithAnUnknownEmailAnswers401() throws Exception {
        mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("nobody@example.com", "secret123")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void aUserWithoutPasswordCannotLogIn() throws Exception {
        // Like the channel users created from the videoGrabber
        User channel = new User();
        channel.setUsername("Music Channel");
        channel.setEmail("channel@example.com");
        userRepository.save(channel);

        mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("channel@example.com", "secret123")))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void whoAmIWithoutLoginAnswers401() throws Exception {
        mockMvc.perform(get(ME_URL))
                .andExpect(status().isUnauthorized());
    }

    // ----- Logout: private pages are closed again -----

    @Test
    void logoutAnswers200() throws Exception {
        MockHttpSession session = logInAsAnna();

        mockMvc.perform(post(LOGOUT_URL).session(session))
                .andExpect(status().isOk());
    }

    @Test
    void afterLogoutPrivatePagesAnswer401() throws Exception {
        MockHttpSession session = logInAsAnna();
        mockMvc.perform(post(LOGOUT_URL).session(session));

        mockMvc.perform(get(ME_URL).session(session))
                .andExpect(status().isUnauthorized());
    }

    // ----- Helpers -----

    private MockHttpSession logInAsAnna() throws Exception {
        MvcResult result = mockMvc.perform(post(LOGIN_URL).contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody("anna@example.com", "secret123")))
                .andExpect(status().isOk())
                .andReturn();
        return (MockHttpSession) result.getRequest().getSession(false);
    }

    private String loginBody(String email, String password) {
        return "{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}";
    }
}
