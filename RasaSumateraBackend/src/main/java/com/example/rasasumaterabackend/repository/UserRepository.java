package com.example.rasasumaterabackend.repository;

import com.example.rasasumaterabackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Mencari user berdasarkan email (untuk login)
    Optional<User> findByEmail(String email);

    // Memeriksa ada tidaknya email di database (untuk register)
    boolean existsByEmail(String email);

    // Memeriksa ada tidaknya username di database (untuk register)
    boolean existsByUsername(String username);
}