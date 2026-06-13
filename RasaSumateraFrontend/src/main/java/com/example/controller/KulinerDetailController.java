package com.example.controller;

import com.example.model.Kuliner;
import com.example.service.ApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class KulinerDetailController {

    @FXML private Label lblNama;
    @FXML private Label lblDaerah;
    @FXML private Label lblDeskripsi;

    private final ApiService apiService = new ApiService();

    /**
     * Dipanggil dari KulinerListController untuk memberi tahu kuliner mana
     * yang profilnya harus ditampilkan.
     */
    public void setKulinerId(Long id) {
        loadDetail(id);
    }

    /**
     * Mengambil detail kuliner dari backend berdasarkan ID dan menampilkannya.
     */
    private void loadDetail(Long id) {
        lblNama.setText("Memuat...");
        lblDaerah.setText("");
        lblDeskripsi.setText("Memuat deskripsi...");

        new Thread(() -> {
            try {
                Kuliner k = apiService.getKulinerDetail(id);
                Platform.runLater(() -> {
                    lblNama.setText(k.getNama());
                    lblDaerah.setText("📍 " + k.getDaerah());
                    lblDeskripsi.setText(k.getDeskripsi());
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblNama.setText("Gagal memuat data");
                    lblDeskripsi.setText("Pastikan server backend berjalan, lalu coba lagi.");
                });
            }
        }).start();
    }

    /**
     * Kembali ke halaman daftar kuliner.
     */
    @FXML
    void handleKembali(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/kuliner-list-view.fxml"));
            Parent root = loader.load();

            Node source = lblNama;
            Stage stage = (Stage) source.getScene().getWindow();
            Scene scene = source.getScene();
            scene.setRoot(root);
            stage.setScene(scene);
        } catch (IOException e) {
            lblDeskripsi.setText("Gagal kembali ke halaman daftar kuliner.");
        }
    }
}
