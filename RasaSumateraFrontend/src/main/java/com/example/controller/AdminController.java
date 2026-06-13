package com.example.controller;

import com.example.model.Kuliner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class AdminController {

    @FXML private TableView<Kuliner> tableKuliner;
    @FXML private TableColumn<Kuliner, String> colNama;
    @FXML private TableColumn<Kuliner, String> colDaerah;
    @FXML private TableColumn<Kuliner, String> colDeskripsi;

    @FXML private TextField inputNamaKuliner;
    @FXML private ComboBox<String> comboDaerah;
    @FXML private TextArea inputDeskripsi;
    @FXML private Button btnSimpan;
    @FXML private Button btnHapus;

    // List untuk menampung data di tabel (Observable agar UI auto-update)
    private ObservableList<Kuliner> listKuliner = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // 1. Inisialisasi Pilihan Daerah
        comboDaerah.setItems(FXCollections.observableArrayList(
                "Aceh", "Sumatera Utara", "Sumatera Barat", "Riau", "Kepulauan Riau", "Jambi"
        ));

        // 2. Hubungkan kolom tabel dengan variabel di model Kuliner
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colDaerah.setCellValueFactory(new PropertyValueFactory<>("daerah"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));

        // 3. Tambahkan Data Mockup (Contoh)
        listKuliner.add(new Kuliner("Bika Ambon", "Sumatera Utara", "Kue kenyal berwarna kuning dengan rongga khas.", ""));
        listKuliner.add(new Kuliner("Rendang", "Sumatera Barat", "Daging sapi yang dimasak lama dengan rempah dan santan.", ""));

        tableKuliner.setItems(listKuliner);
    }

    @FXML
    void handleSimpan(ActionEvent event) {
        String nama = inputNamaKuliner.getText();
        String daerah = comboDaerah.getValue();
        String deskripsi = inputDeskripsi.getText();
        String gambarUrl = "";

        // Validasi Sederhana
        if (nama.isEmpty() || daerah == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Nama dan Daerah wajib diisi!");
            alert.show();
            return;
        }

        // Tambah data ke list
        listKuliner.add(new Kuliner(nama, daerah, deskripsi, gambarUrl));

        // Reset Input
        inputNamaKuliner.clear();
        inputDeskripsi.clear();
        comboDaerah.setValue(null);
    }

    @FXML
    void handleHapus(ActionEvent event) {
        Kuliner terpilih = tableKuliner.getSelectionModel().getSelectedItem();
        if (terpilih != null) {
            listKuliner.remove(terpilih);
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Pilih data di tabel dulu untuk dihapus!");
            alert.show();
        }
    }
}