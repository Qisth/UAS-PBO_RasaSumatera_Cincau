package com.example.model;

public class Ulasan {
    private Long id;
    private String namaPengguna;
    private String isiUlasan;
    private int rating; // Jika nanti ingin ditambahkan sistem bintang (1-5)

    // Constructor Kosong
    public Ulasan() {
    }

    // Constructor Lengkap
    public Ulasan(Long id, String namaPengguna, String isiUlasan, int rating) {
        this.id = id;
        this.namaPengguna = namaPengguna;
        this.isiUlasan = isiUlasan;
        this.rating = rating;
    }

    // Getter dan Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamaPengguna() {
        return namaPengguna;
    }

    public void setNamaPengguna(String namaPengguna) {
        this.namaPengguna = namaPengguna;
    }

    public String getIsiUlasan() {
        return isiUlasan;
    }

    public void setIsiUlasan(String isiUlasan) {
        this.isiUlasan = isiUlasan;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}