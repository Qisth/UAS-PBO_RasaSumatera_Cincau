package com.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.shape.Circle;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class MainController {

    @FXML private Button btnLoginNav;
    @FXML private Button btnKirimUlasan;
    @FXML private TextArea inputUlasan;
    @FXML private Circle circleHeroImg;

    @FXML
    public void initialize() {
        // Tempat inisialisasi awal saat UI dirender
    }

    @FXML
    void handleNavLogin(ActionEvent event) {
        System.out.println("Tombol Login di Navbar diklik! Membuka pop-up autentikasi.");
    }

    @FXML
    void handleKirimUlasan(ActionEvent event) {
        String ulasanText = inputUlasan.getText().trim();

        // Implementasi Validasi input sesuai syarat nomor 6
        if (ulasanText.isEmpty()) {
            showAlert(AlertType.WARNING, "Validasi Gagal", "Ulasan tidak boleh kosong!");
            return;
        }

        System.out.println("Mengirim ulasan ke service backend: " + ulasanText);
        showAlert(AlertType.INFORMATION, "Sukses", "Ulasan kamu berhasil terkirim!");
        inputUlasan.clear();
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}