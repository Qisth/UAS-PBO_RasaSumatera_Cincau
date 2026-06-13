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
                // Menonaktifkan CSRF karena aplikasi diakses via REST API Client (JavaFX), bukan form HTML browser
                .csrf(csrf -> csrf.disable())

                // Mengubah manajemen session menjadi STATELESS (tidak menyimpan session di memori server)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Mengatur hak akses route/endpoint URL
                .authorizeHttpRequests(auth -> auth
                        // Mengizinkan akses tanpa token untuk login dan register
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Mengizinkan konsol H2 database dibuka secara bebas selama masa pengembangan proyek
                        .requestMatchers("/h2-console/**").permitAll()
                        // Endpoint di luar itu (seperti menambah ulasan) wajib membawa Token JWT
                        .anyRequest().authenticated()
                );

        // Mencegah proteksi X-Frame-Options memblokir tampilan halaman H2 Console di browser
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));

        // Menyisipkan filter JWT buatan kita sebelum filter bawaan Spring Security dijalankan
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}