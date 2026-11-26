package com.tecnocampus.LS2.protube_back.controller;

import com.tecnocampus.LS2.protube_back.configuration.SecurityConfig;
import com.tecnocampus.LS2.protube_back.dto.RegisterRequest;
import com.tecnocampus.LS2.protube_back.dto.UserResponse;
import com.tecnocampus.LS2.protube_back.services.EmailAlreadyUsedException;
import com.tecnocampus.LS2.protube_back.services.InvalidRegistrationException;
import com.tecnocampus.LS2.protube_back.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Calls POST /api/auth/register (without starting the server) with the real security rules.
// The requests are sent WITHOUT a CSRF token: CSRF is disabled (decision taken in PG-26).
@WebMvcTest(controllers = AuthController.class, properties = "pro_tube.store.dir=/tmp/")
@Import(SecurityConfig.class)
class AuthControllerWebTest {

    private static final String REGISTER_URL = "/api/auth/register";
    private static final String GOOD_BODY = "{\"email\": \"anna@example.com\", \"password\": \"secret123\"}";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void shouldReturnStatus201AndTheNewUser() throws Exception {
        UserResponse newUser = new UserResponse(1L, "anna", "anna@example.com");
        when(userService.register(any(RegisterRequest.class))).thenReturn(newUser);

        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(GOOD_BODY))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("anna"))
                .andExpect(jsonPath("$.email").value("anna@example.com"));
    }

    @Test
    void shouldNeverSendThePasswordBack() throws Exception {
        UserResponse newUser = new UserResponse(1L, "anna", "anna@example.com");
        when(userService.register(any(RegisterRequest.class))).thenReturn(newUser);

        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(GOOD_BODY))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void shouldReturnStatus409WhenTheEmailIsAlreadyUsed() throws Exception {
        when(userService.register(any(RegisterRequest.class))).thenThrow(new EmailAlreadyUsedException());

        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(GOOD_BODY))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnStatus400WhenTheDataIsWrong() throws Exception {
        when(userService.register(any(RegisterRequest.class)))
                .thenThrow(new InvalidRegistrationException("Email is not valid"));

        mockMvc.perform(post(REGISTER_URL).contentType(MediaType.APPLICATION_JSON).content(GOOD_BODY))
                .andExpect(status().isBadRequest());
    }
}
