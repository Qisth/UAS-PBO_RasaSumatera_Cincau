package com.example.rasasumaterabackend.service;

import com.example.rasasumaterabackend.model.User;
import com.example.rasasumaterabackend.repository.UserRepository;
import com.example.rasasumaterabackend.config.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider tokenProvider;

    // Mendaftarkan user baru dengan validasi keunikan email dan username
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email sudah terdaftar!");
        }
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username sudah digunakan!");
        }

        // Hashing password sebelum disimpan ke database H2 demi keamanan
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    // Memvalidasi login menggunakan email dan password, lalu menghasilkan token JWT.
    public String loginUser(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email atau password salah!"));

        // Mencocokkan password inputan plain text dengan hash yang ada di database
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Email atau password salah!");
        }

        // Jika kredensial cocok, generate token JWT berbasis Email
        return tokenProvider.generateToken(user.getEmail());
    }

    // Mengambil data profil user berdasarkan email (diperlukan saat validasi token JWT).
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}