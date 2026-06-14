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
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;

public class KulinerDetailController {

    @FXML private Circle circleGambar;
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

                    try {
                        // Ambil nama file dari model kuliner (misal: "rendang.jpg")
                        String namaFile = k.getGambarUrl();

                        // Buat path lengkap menuju folder resources internal
                        String pathInternal = "/com/example/images/" + namaFile;

                        // Cari url internal system-nya
                        var resourceUrl = getClass().getResource(pathInternal);

                        if (resourceUrl != null) {
                            // .toExternalForm() akan mengubahnya menjadi format file:/ atau jar:/ yang aman
                            Image fotoKuliner = new Image(resourceUrl.toExternalForm());
                            ImagePattern pattern = new ImagePattern(fotoKuliner);
                            circleGambar.setFill(pattern);
                        } else {
                            // Jika nama file di database tidak ditemukan di folder images
                            circleGambar.setFill(javafx.scene.paint.Color.web("#FCEBEB"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        circleGambar.setFill(javafx.scene.paint.Color.web("#FCEBEB"));
                    }
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

            // Solusi Bersih: Mengambil Stage langsung dari event tombol yang diklik
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Membuat Scene baru agar window otomatis menyesuaikan diri dengan dimensi list view
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            lblDeskripsi.setText("Gagal kembali ke halaman daftar kuliner.");
        }
    }
}
