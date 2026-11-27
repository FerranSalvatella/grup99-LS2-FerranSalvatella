package com.tecnocampus.LS2.protube_back.services;

import com.tecnocampus.LS2.protube_back.entity.User;
import com.tecnocampus.LS2.protube_back.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// Tells Spring Security how to find a user to log in: by email.
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public AppUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> foundUser = userRepository.findByEmail(email);
        if (foundUser.isEmpty()) {
            throw new UsernameNotFoundException("There is no user with this email");
        }

        User user = foundUser.get();
        if (user.getPasswordHash() == null) {
            // Channel users created by the videoGrabber cannot log in
            throw new UsernameNotFoundException("This user cannot log in");
        }

        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();
    }
}
