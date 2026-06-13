package com.example.rasasumaterabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "kuliner")
public class Kuliner extends BaseEntity {

    // Encapsulation: Getter dan Setter
    @NotBlank(message = "Nama kuliner tidak boleh kosong")
    @Column(nullable = false)
    private String nama;

    @NotBlank(message = "Deskripsi kuliner tidak boleh kosong")
    @Column(columnDefinition = "TEXT") // Agar muat teks deskripsi yang panjang di H2
    private String deskripsi;

    @NotBlank(message = "URL Gambar tidak boleh kosong")
    @Column(name = "image_url")
    private String imageUrl;

    // Relasi Many-to-One ke Daerah
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daerah_id", nullable = false)
    private Daerah daerah;

    // Constructor Kosong
    public Kuliner() {}

    // Polymorphism: Overloading Constructor
    public Kuliner(String nama, String deskripsi, String imageUrl, Daerah daerah) {
        this.nama = nama;
        this.deskripsi = deskripsi;
        this.imageUrl = imageUrl;
        this.daerah = daerah;
    }

}