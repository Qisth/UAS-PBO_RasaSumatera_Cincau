package com.example.rasasumaterabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "daerah")
public class Daerah {

    // Getters & Setters
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Nama daerah tidak boleh kosong")
    @Size(max = 100, message = "Nama daerah maksimal 100 karakter")
    @Column(name = "nama_daerah", nullable = false, unique = true, length = 100)
    private String namaDaerah;

    @Setter
    @NotBlank(message = "Deskripsi tidak boleh kosong")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String deskripsi;

    @Setter
    @Column(name = "gambar_url")
    private String gambarUrl;

    @Setter
    @OneToMany(mappedBy = "daerah", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Kuliner> kulinerList;

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
    public Daerah() {}

    // Constructor Lengkap
    public Daerah(String namaDaerah, String deskripsi, String gambarUrl) {
        this.namaDaerah = namaDaerah;
        this.deskripsi = deskripsi;
        this.gambarUrl = gambarUrl;
    }

}