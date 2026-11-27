package com.tecnocampus.LS2.protube_back.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService,
                                                       PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(provider);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults());
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests(rules -> {
            rules.requestMatchers(HttpMethod.GET, "/api/videos", "/api/videos/**").permitAll();
            rules.requestMatchers("/api/auth/me").authenticated();
            rules.requestMatchers("/api/auth/**").permitAll();
            rules.requestMatchers("/api/**").authenticated();
            rules.anyRequest().permitAll();
        });

        HttpStatusEntryPoint unauthorized = new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED);
        http.exceptionHandling(errors -> errors.authenticationEntryPoint(unauthorized));

        return http.build();
    }
}
