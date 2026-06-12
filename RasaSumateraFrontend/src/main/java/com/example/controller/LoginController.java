package com.example.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField inputEmail;
    @FXML private PasswordField inputPassword;
    @FXML private Label lblErrorEmail;
    @FXML private Button btnMasuk;

    @FXML
    public void initialize() {
        // Secara default pesan error format email disembunyikan
        lblErrorEmail.setVisible(false);
    }

    @FXML
    void handleLoginSubmit(ActionEvent event) {
        String email = inputEmail.getText().trim();
        String password = inputPassword.getText();

        // Validasi Email Sederhana (Syarat Keamanan & Validasi No 6 & 7)
        if (!email.contains("@") || !email.contains(".")) {
            lblErrorEmail.setVisible(true); // Tampilkan teks merah 'Format email tidak valid'
        } else {
            lblErrorEmail.setVisible(false);
            System.out.println("Melakukan Autentikasi Pengguna untuk: " + email);
            // TODO: Integrasikan token dengan Spring Security backend nanti
        }
    }
}