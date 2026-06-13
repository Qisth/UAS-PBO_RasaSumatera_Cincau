package com.example.model;

public class Kuliner {
    private String nama;
    private String daerah;
    private String deskripsi;
    private String gambarUrl;

    public Kuliner() {}

    public Kuliner(String nama, String daerah, String deskripsi) {
        this.nama = nama;
        this.daerah = daerah;
        this.deskripsi = deskripsi;
        this.gambarUrl = "";
    }

    public Kuliner(String nama, String daerah, String deskripsi, String gambarUrl) {
        this.nama = nama;
        this.daerah = daerah;
        this.deskripsi = deskripsi;
        this.gambarUrl = gambarUrl;
    }

    public String getNama() { return nama; }
    public void setNama(String nama) { this.nama = nama; }

    public String getDaerah() { return daerah; }
    public void setDaerah(String daerah) { this.daerah = daerah; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }

    public String getGambarUrl() { return gambarUrl; }
    public void setGambarUrl(String gambarUrl) { this.gambarUrl = gambarUrl; }
}