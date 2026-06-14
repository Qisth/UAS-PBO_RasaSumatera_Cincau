package com.example.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Kuliner {
    private Long id;
    private Long daerahId;

    private final StringProperty nama;
    private final StringProperty daerah;
    private final StringProperty deskripsi;
    private final StringProperty imageUrl;

    // Constructor
    public Kuliner(String nama, String daerah, String deskripsi, String imageUrl) {
        this.nama = new SimpleStringProperty(nama);
        this.daerah = new SimpleStringProperty(daerah);
        this.deskripsi = new SimpleStringProperty(deskripsi);
        this.imageUrl = new SimpleStringProperty(imageUrl);
    }

    // Getter & Setter untuk id dan daerahId (data dari backend)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getDaerahId() { return daerahId; }
    public void setDaerahId(Long daerahId) { this.daerahId = daerahId; }

    // Getter untuk Property (Dibutuhkan oleh PropertyValueFactory JavaFX)
    public StringProperty namaProperty() { return nama; }
    public StringProperty daerahProperty() { return daerah; }
    public StringProperty deskripsiProperty() { return deskripsi; }
    public StringProperty imageUrlProperty() { return imageUrl; }

    // Getter Standar
    public String getNama() { return nama.get(); }
    public String getDaerah() { return daerah.get(); }
    public String getDeskripsi() { return deskripsi.get(); }
    public String getimageUrl() { return imageUrl.get(); }

    // Setter Standar
    public void setNama(String value) { nama.set(value); }
    public void setDaerah(String value) { daerah.set(value); }
    public void setDeskripsi(String value) { deskripsi.set(value); }
    public void setImageUrl(String value) { imageUrl.set(value); }
}