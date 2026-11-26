package com.tecnocampus.LS2.protube_back.repository;

import com.tecnocampus.LS2.protube_back.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveUserAndGiveItAnId() {
        User savedUser = userRepository.save(createUser("anna", "anna@example.com"));

        assertNotNull(savedUser.getId());
    }

    @Test
    void shouldSaveAllTheFieldsOfTheUser() {
        User user = createUser("anna", "anna@example.com");
        user.setDescription("I like cooking videos");
        user.setAvatarUrl("anna.png");
        user.setCreatedAt(LocalDateTime.of(2026, 11, 26, 10, 0));
        User savedUser = userRepository.save(user);

        User foundUser = userRepository.findById(savedUser.getId()).orElseThrow();

        assertEquals("anna", foundUser.getUsername());
        assertEquals("anna@example.com", foundUser.getEmail());
        assertEquals("hash-of-the-password", foundUser.getPasswordHash());
        assertEquals("I like cooking videos", foundUser.getDescription());
        assertEquals("anna.png", foundUser.getAvatarUrl());
        assertEquals(LocalDateTime.of(2026, 11, 26, 10, 0), foundUser.getCreatedAt());
    }

    @Test
    void shouldFindUserByUsername() {
        userRepository.save(createUser("anna", "anna@example.com"));

        Optional<User> foundUser = userRepository.findByUsername("anna");

        assertTrue(foundUser.isPresent());
        assertEquals("anna@example.com", foundUser.get().getEmail());
    }

    @Test
    void shouldFindUserByEmail() {
        userRepository.save(createUser("anna", "anna@example.com"));

        Optional<User> foundUser = userRepository.findByEmail("anna@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("anna", foundUser.get().getUsername());
    }

    @Test
    void shouldReturnEmptyWhenTheEmailDoesNotExist() {
        Optional<User> foundUser = userRepository.findByEmail("nobody@example.com");

        assertFalse(foundUser.isPresent());
    }

    @Test
    void shouldNotSaveTwoUsersWithTheSameEmail() {
        userRepository.saveAndFlush(createUser("anna", "anna@example.com"));

        User sameEmail = createUser("other", "anna@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(sameEmail));
    }

    @Test
    void shouldNotSaveTwoUsersWithTheSameUsername() {
        userRepository.saveAndFlush(createUser("anna", "anna@example.com"));

        User sameUsername = createUser("anna", "other@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> userRepository.saveAndFlush(sameUsername));
    }

    private User createUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash("hash-of-the-password");
        return user;
    }
}
