package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.entity.User;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

// Spring Security calls loadUserByUsername during the login.
// In ProTube the login is done with the email, so the "username" received is the email.
@ExtendWith(MockitoExtension.class)
class AppUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    private AppUserDetailsService appUserDetailsService;

    @BeforeEach
    void setUp() {
        appUserDetailsService = new AppUserDetailsService(userRepository);
    }

    @Test
    void shouldLoadTheUserByEmail() {
        User anna = new User();
        anna.setUsername("anna");
        anna.setEmail("anna@example.com");
        anna.setPasswordHash("hash-of-the-password");
        when(userRepository.findByEmail("anna@example.com")).thenReturn(Optional.of(anna));

        UserDetails details = appUserDetailsService.loadUserByUsername("anna@example.com");

        assertEquals("anna@example.com", details.getUsername());
        assertEquals("hash-of-the-password", details.getPassword());
    }

    @Test
    void shouldFailWhenTheEmailDoesNotExist() {
        when(userRepository.findByEmail("nobody@example.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class,
                () -> appUserDetailsService.loadUserByUsername("nobody@example.com"));
    }

    @Test
    void shouldFailForAUserWithoutPassword() {
        // The channel users created from the videoGrabber have no password: they cannot log in
        User channel = new User();
        channel.setUsername("Music Channel");
        channel.setEmail("channel@example.com");
        when(userRepository.findByEmail("channel@example.com")).thenReturn(Optional.of(channel));

        assertThrows(UsernameNotFoundException.class,
                () -> appUserDetailsService.loadUserByUsername("channel@example.com"));
    }
}
