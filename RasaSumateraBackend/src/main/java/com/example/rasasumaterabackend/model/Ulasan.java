package com.example.rasasumaterabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "ulasan")
public class Ulasan extends BaseEntity {

    // Encapsulation: Getter dan Setter
    // Menghubungkan ke entitas User untuk mendapatkan data user (termasuk username)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Isi ulasan tidak boleh kosong")
    @Column(columnDefinition = "TEXT", nullable = false)
    private String isiUlasan;

    @NotNull(message = "Rating wajib diisi")
    @Min(value = 1, message = "Rating minimal bernilai 1")
    @Max(value = 5, message = "Rating maksimal bernilai 5")
    @Column(nullable = false)
    private Integer rating;

    // Menghubungkan ke Kuliner mana ulasan ini diberikan
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "kuliner_id", nullable = false)
    private Kuliner kuliner;

    // Constructor Kosong
    public Ulasan() {}

    // Polymorphism: Overloading Constructor
    public Ulasan(User user, String isiUlasan, Integer rating, Kuliner kuliner) {
        this.user = user;
        this.isiUlasan = isiUlasan;
        this.rating = rating;
        this.kuliner = kuliner;
    }

    // Metode Tambahan untuk Memenuhi Request Khusus Anda:
    // JavaFX bisa langsung memanggil getUsernamePengulas() untuk menampilkan nama pengulas
    public String getUsernamePengulas() {
        return this.user != null ? this.user.getUsername() : "Anonymous";
    }

}