package com.example.model;

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.StringProperty;

public class Daerah {
    private final LongProperty id;
    private final StringProperty nama;

    public Daerah(Long id, String nama) {
        this.id = new SimpleLongProperty(id == null ? -1 : id);
        this.nama = new SimpleStringProperty(nama);
    }

    public LongProperty idProperty() { return id; }
    public StringProperty namaProperty() { return nama; }

    public Long getId() { return id.get(); }
    public String getNama() { return nama.get(); }

    public void setId(Long value) { id.set(value); }
    public void setNama(String value) { nama.set(value); }

    @Override
    public String toString() {
        return nama.get();
    }
}
