package com.example.controller;

import com.example.model.Kuliner;
import com.example.model.Ulasan;
import com.example.model.UlasanResponse;
import com.example.service.ApiService;
import com.example.util.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class KulinerDetailController {

    @FXML private Circle circleGambar;
    @FXML private Label lblNama;
    @FXML private Label lblDaerah;
    @FXML private Label lblDeskripsi;
    @FXML private TextArea inputUlasan;
    @FXML private ComboBox<Integer> cmbRating;
    @FXML private VBox ulasanContainer;

    private Long kulinerId;

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {

        cmbRating.getItems().addAll(1, 2, 3, 4, 5);

        // Opsional: default rating 5
        cmbRating.setValue(5);
    }

    /**
     * Dipanggil dari KulinerListController untuk memberi tahu kuliner mana
     * yang profilnya harus ditampilkan.
     */
    public void setKulinerId(Long id) {
        loadDetail(id);
        this.kulinerId = id;
        loadUlasan();
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

    @FXML
    void handleKirimUlasan(ActionEvent event) {

        if (SessionManager.getToken() == null ||
                SessionManager.getToken().isBlank()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Belum Login",
                    "Silakan login terlebih dahulu."
            );
            return;
        }

        String isi = inputUlasan.getText().trim();
        Integer rating = cmbRating.getValue();

        if (isi.isEmpty()) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validasi",
                    "Ulasan tidak boleh kosong."
            );
            return;
        }

        if (rating == null) {

            showAlert(
                    Alert.AlertType.WARNING,
                    "Validasi",
                    "Silakan pilih rating terlebih dahulu."
            );
            return;
        }

        try {

            apiService.kirimUlasan(kulinerId, isi, rating);

            showAlert(
                    Alert.AlertType.INFORMATION,
                    "Berhasil",
                    "Ulasan berhasil dikirim."
            );

            inputUlasan.clear();
            cmbRating.setValue(null);

            // Refresh daftar ulasan jika ada
            loadUlasan();

        } catch (Exception e) {

            showAlert(
                    Alert.AlertType.ERROR,
                    "Gagal",
                    e.getMessage()
            );

            e.printStackTrace();
        }
    }

    private void loadUlasan() {
        try {
            List<UlasanResponse> ulasan = apiService.getUlasanByKuliner(kulinerId);
            tampilkanUlasan(ulasan);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tampilkanUlasan(List<UlasanResponse> daftarUlasan) {
        ulasanContainer.getChildren().clear();
        for (UlasanResponse ulasan : daftarUlasan) {
            VBox card = new VBox(5);
            card.setStyle(
                    "-fx-background-color: #F5F5F5;" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 10;"
            );
            Label lblUser = new Label(ulasan.getPengulas());
            lblUser.setStyle(
                    "-fx-font-weight: bold;" +
                            "-fx-font-size: 13;"
            );
            Label lblRating = new Label(
                    "⭐ " + ulasan.getRating() + "/5"
            );
            Label lblIsi = new Label(
                    ulasan.getIsiUlasan()
            );
            lblIsi.setWrapText(true);
            card.getChildren().addAll(
                    lblUser,
                    lblRating,
                    lblIsi
            );
            ulasanContainer.getChildren().add(card);
        }
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
