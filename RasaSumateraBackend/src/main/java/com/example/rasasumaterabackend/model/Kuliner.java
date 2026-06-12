package com.example.rasasumaterabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name = "kuliner")
public class Kuliner {

    // Getters & Setters
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Nama kuliner tidak boleh kosong")
    @Size(max = 100, message = "Nama maksimal 100 karakter")
    @Column(nullable = false, length = 100)
    private String nama;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daerah_id", nullable = false)
    private Daerah daerah;

    @Setter
    @Column(columnDefinition = "TEXT")
    private String deskripsi;

    @Setter
    @Column(name = "gambar_url")
    private String gambarUrl;

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

    // Constructor kosong (wajib untuk JPA)
    public Kuliner() {}

    // Constructor lengkap
    public Kuliner(String nama, Daerah daerah, String deskripsi, String gambarUrl) {
        this.nama = nama;
        this.daerah = daerah;
        this.deskripsi = deskripsi;
        this.gambarUrl = gambarUrl;
    }

}