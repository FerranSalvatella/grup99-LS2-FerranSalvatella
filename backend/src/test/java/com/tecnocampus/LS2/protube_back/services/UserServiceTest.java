package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.dto.RegisterRequest;
import com.tecnocampus.LS2.protube_back.dto.UserResponse;
import com.tecnocampus.LS2.protube_back.entity.User;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    private static final String INVALID_EMAIL_MESSAGE = "Email is not valid";
    private static final String INVALID_PASSWORD_MESSAGE =
            "Password must have at least 8 characters, a letter and a number";
    private static final String EMAIL_USED_MESSAGE = "Email is already used";

    @Mock
    private UserRepository userRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userRepository, passwordEncoder);
    }

    // ----- Good email and password: the account is created -----

    @Test
    void shouldCreateTheAccountWithAGoodEmailAndPassword() {
        returnTheSameUserWhenSaving();

        UserResponse response = userService.register(new RegisterRequest("anna@example.com", "secret123"));

        assertEquals("anna@example.com", response.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldSaveThePasswordAsABCryptHash() {
        returnTheSameUserWhenSaving();

        userService.register(new RegisterRequest("anna@example.com", "secret123"));

        User savedUser = getSavedUser();
        assertNotEquals("secret123", savedUser.getPasswordHash());
        assertTrue(passwordEncoder.matches("secret123", savedUser.getPasswordHash()));
    }

    @Test
    void shouldSaveTheCreationDate() {
        returnTheSameUserWhenSaving();

        userService.register(new RegisterRequest("anna@example.com", "secret123"));

        assertNotNull(getSavedUser().getCreatedAt());
    }

    @Test
    void shouldAcceptAPasswordOfExactly8CharactersWithALetterAndANumber() {
        returnTheSameUserWhenSaving();

        UserResponse response = userService.register(new RegisterRequest("anna@example.com", "abcdefg1"));

        assertEquals("anna@example.com", response.getEmail());
    }

    // ----- The username is made from the email (the part before the @) -----

    @Test
    void shouldMakeTheUsernameFromTheEmail() {
        returnTheSameUserWhenSaving();

        UserResponse response = userService.register(new RegisterRequest("john.smith@example.com", "secret123"));

        assertEquals("john.smith", response.getUsername());
        assertEquals("john.smith", getSavedUser().getUsername());
    }

    @Test
    void shouldAddTheNumber2WhenTheUsernameIsAlreadyTaken() {
        returnTheSameUserWhenSaving();
        when(userRepository.findByUsername("anna")).thenReturn(Optional.of(new User()));

        UserResponse response = userService.register(new RegisterRequest("anna@other.com", "secret123"));

        assertEquals("anna2", response.getUsername());
    }

    @Test
    void shouldTryTheNextNumberUntilTheUsernameIsFree() {
        returnTheSameUserWhenSaving();
        when(userRepository.findByUsername("anna")).thenReturn(Optional.of(new User()));
        when(userRepository.findByUsername("anna2")).thenReturn(Optional.of(new User()));

        UserResponse response = userService.register(new RegisterRequest("anna@third.com", "secret123"));

        assertEquals("anna3", response.getUsername());
    }

    // ----- Email already used: error and no new account -----

    @Test
    void shouldRejectAnEmailThatIsAlreadyUsed() {
        when(userRepository.findByEmail("anna@example.com")).thenReturn(Optional.of(new User()));
        RegisterRequest request = new RegisterRequest("anna@example.com", "secret123");

        EmailAlreadyUsedException error =
                assertThrows(EmailAlreadyUsedException.class, () -> userService.register(request));

        assertEquals(EMAIL_USED_MESSAGE, error.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ----- Wrong email: error and no new account -----

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"annaexample.com", "anna@example", "@example.com", "anna@", "an na@example.com"})
    void shouldRejectAWrongEmail(String wrongEmail) {
        RegisterRequest request = new RegisterRequest(wrongEmail, "secret123");

        InvalidRegistrationException error =
                assertThrows(InvalidRegistrationException.class, () -> userService.register(request));

        assertEquals(INVALID_EMAIL_MESSAGE, error.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ----- Password too weak: error and no new account -----

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"abc1234", "onlyletters", "12345678"})
    void shouldRejectAPasswordThatIsTooWeak(String weakPassword) {
        RegisterRequest request = new RegisterRequest("anna@example.com", weakPassword);

        InvalidRegistrationException error =
                assertThrows(InvalidRegistrationException.class, () -> userService.register(request));

        assertEquals(INVALID_PASSWORD_MESSAGE, error.getMessage());
        verify(userRepository, never()).save(any(User.class));
    }

    // ----- Helpers -----

    // The mocked repository gives back the same user it receives (like a real save)
    private void returnTheSameUserWhenSaving() {
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    private User getSavedUser() {
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        return captor.getValue();
    }
}
