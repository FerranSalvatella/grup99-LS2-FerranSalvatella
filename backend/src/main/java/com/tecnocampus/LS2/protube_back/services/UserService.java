package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.dto.RegisterRequest;
import com.tecnocampus.LS2.protube_back.dto.UserResponse;
import com.tecnocampus.LS2.protube_back.entity.User;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse register(RegisterRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (!isValidEmail(email)) {
            throw new InvalidRegistrationException("Email is not valid");
        }

        if (!isValidPassword(password)) {
            throw new InvalidRegistrationException(
                    "Password must have at least 8 characters, a letter and a number");
        }

        if (userRepository.findByEmail(email).isPresent()) {
            throw new EmailAlreadyUsedException();
        }

        User user = new User();
        user.setEmail(email);
        user.setUsername(createUsername(email));
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return new UserResponse(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail());
    }

    private boolean isValidEmail(String email) {
        if (email == null) {
            return false;
        }

        String emailPattern = "[^@\\s]+@[^@\\s.]+(\\.[^@\\s.]+)+";
        return email.matches(emailPattern);
    }

    private boolean isValidPassword(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }

        boolean hasLetter = false;
        boolean hasDigit = false;

        for (char character : password.toCharArray()) {
            if (Character.isLetter(character)) {
                hasLetter = true;
            }
            if (Character.isDigit(character)) {
                hasDigit = true;
            }
        }

        return hasLetter && hasDigit;
    }

    private String createUsername(String email) {
        String baseName = email.substring(0, email.indexOf('@'));
        String username = baseName;
        int suffix = 2;

        while (userRepository.findByUsername(username).isPresent()) {
            username = baseName + suffix;
            suffix++;
        }

        return username;
    }
}
