package com.example.rasasumaterabackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "daerah")
public class Daerah extends BaseEntity {

    // Encapsulation: Getter dan Setter
    @NotBlank(message = "Nama daerah tidak boleh kosong")
    @Column(nullable = false, unique = true)
    private String nama;

    // Relasi One-to-Many ke Kuliner
    // MappedBy merujuk ke nama properti 'daerah' yang ada di kelas Kuliner
    @OneToMany(mappedBy = "daerah", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Kuliner> daftarKuliner = new ArrayList<>();

    // Constructor Kosong (Wajib JPA)
    public Daerah() {}

    // Polymorphism: Overloading Constructor
    public Daerah(String nama) {
        this.nama = nama;
    }

}