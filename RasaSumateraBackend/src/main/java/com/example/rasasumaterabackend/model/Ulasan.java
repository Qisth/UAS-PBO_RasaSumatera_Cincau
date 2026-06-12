package com.example.rasasumaterabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "ulasan")
public class Ulasan {

    // Getters & Setters
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Nama pengguna tidak boleh kosong")
    @Size(max = 100, message = "Nama pengguna maksimal 100 karakter")
    @Column(name = "nama_pengguna", nullable = false, length = 100)
    private String namaPengguna;

    @Setter
    @NotBlank(message = "Isi ulasan tidak boleh kosong")
    @Size(min = 10, message = "Isi ulasan minimal 10 karakter")
    @Column(name = "isi_ulasan", nullable = false, columnDefinition = "TEXT")
    private String isiUlasan;

    @Setter
    @Min(value = 1, message = "Rating minimal 1")
    @Max(value = 5, message = "Rating maksimal 5")
    @Column(nullable = false)
    private int rating;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kuliner_id", nullable = false)
    private Kuliner kuliner;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Lifecycle Hooks
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Constructor Kosong (wajib untuk JPA)
    public Ulasan() {}

    // Constructor Lengkap
    public Ulasan(String namaPengguna, String isiUlasan, int rating, Kuliner kuliner) {
        this.namaPengguna = namaPengguna;
        this.isiUlasan = isiUlasan;
        this.rating = rating;
        this.kuliner = kuliner;
    }

}