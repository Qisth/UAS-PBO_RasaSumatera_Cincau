package com.example.rasasumaterabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "users") // Menggunakan nama 'users' karena 'user' sering kali merupakan kata kunci terpesan di beberapa database
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username tidak boleh kosong")
    @Size(min = 4, max = 50, message = "Username harus berukuran antara 4 sampai 50 karakter")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Format email tidak valid")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password tidak boleh kosong")
    @Size(min = 6, message = "Password minimal harus terdiri dari 6 karakter")
    @Column(nullable = false)
    private String password; // Catatan Keamanan: Nilai yang disimpan di sini harus berupa hash (misal: BCrypt), bukan plain text

    // Constructor Kosong (Wajib untuk JPA)
    public User() {
    }

    // Constructor dengan Parameter
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

}