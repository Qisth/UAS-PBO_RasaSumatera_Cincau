package com.example.rasasumaterabackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Menggunakan enkripsi algoritma BCrypt yang aman untuk database H2
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // 1. Jalur Utama Autentikasi & Console Database
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        // 2. Proteksi Fitur DAERAH (GET umum, sisanya ADMIN)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/daerah/**").permitAll()
                        .requestMatchers("/api/v1/daerah/**").hasRole("ADMIN")

                        // 3. Proteksi Fitur KULINER (GET umum, sisanya ADMIN)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/kuliner/**").permitAll()
                        .requestMatchers("/api/v1/kuliner/**").hasRole("ADMIN")

                        // 4. Proteksi Fitur ULASAN (GET umum, sisanya WAJIB LOGIN)
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/v1/ulasan/**").permitAll()
                        .requestMatchers("/api/v1/ulasan/**").authenticated()

                        // 5. Semua request lain yang tidak terdaftar wajib login
                        .anyRequest().authenticated()
                );

        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}