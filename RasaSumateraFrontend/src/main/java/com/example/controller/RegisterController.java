package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * RegisterController — menangani logika halaman Daftar
 * FXML : Register.fxml
 */
public class RegisterController implements Initializable {

    // ── Field FXML ──────────────────────────────────────────
    @FXML private TextField     inputNama;
    @FXML private TextField     inputEmail;
    @FXML private PasswordField inputPassword;
    @FXML private TextField     inputPasswordVisible;
    @FXML private Button        btnTogglePassword;
    @FXML private PasswordField inputPassword2;
    @FXML private TextField     inputPassword2Visible;
    @FXML private Button        btnTogglePassword2;

    @FXML private Label         lblErrorEmail;
    @FXML private Label         lblErrorPassword;
    @FXML private Label         lblErrorPassword2;
    @FXML private Label         lblPasswordStrength;
    @FXML private ProgressBar   passwordStrengthBar;

    @FXML private Button        btnDaftar;

    private boolean pw1Visible = false;
    private boolean pw2Visible = false;

    // ── Inisialisasi ────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Sembunyikan semua label error
        hideLabel(lblErrorEmail);
        hideLabel(lblErrorPassword);
        hideLabel(lblErrorPassword2);

        // Sync password field 1
        inputPassword.textProperty().addListener((obs, old, val) -> {
            if (!pw1Visible) inputPasswordVisible.setText(val);
            updatePasswordStrength(val);
            validatePassword(val);
        });
        inputPasswordVisible.textProperty().addListener((obs, old, val) -> {
            if (pw1Visible) {
                inputPassword.setText(val);
                updatePasswordStrength(val);
                validatePassword(val);
            }
        });

        // Sync password field 2
        inputPassword2.textProperty().addListener((obs, old, val) -> {
            if (!pw2Visible) inputPassword2Visible.setText(val);
            validatePassword2(val);
        });
        inputPassword2Visible.textProperty().addListener((obs, old, val) -> {
            if (pw2Visible) {
                inputPassword2.setText(val);
                validatePassword2(val);
            }
        });

        // Validasi email real-time
        inputEmail.textProperty().addListener((obs, old, val) -> validateEmail(val));
    }

    // ── Toggle password 1 ───────────────────────────────────
    @FXML
    private void togglePassword() {
        pw1Visible = !pw1Visible;
        swapFields(inputPassword, inputPasswordVisible, pw1Visible, btnTogglePassword);
    }

    // ── Toggle password 2 ───────────────────────────────────
    @FXML
    private void togglePassword2() {
        pw2Visible = !pw2Visible;
        swapFields(inputPassword2, inputPassword2Visible, pw2Visible, btnTogglePassword2);
    }

    private void swapFields(PasswordField pf, TextField tf, boolean show, Button btn) {
        if (show) {
            tf.setText(pf.getText());
            tf.setVisible(true); tf.setManaged(true);
            pf.setVisible(false); pf.setManaged(false);
            btn.setText("🙈");
        } else {
            pf.setText(tf.getText());
            pf.setVisible(true); pf.setManaged(true);
            tf.setVisible(false); tf.setManaged(false);
            btn.setText("👁");
        }
    }

    // ── Validasi email ──────────────────────────────────────
    private boolean validateEmail(String val) {
        boolean valid = val.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
        if (val.isEmpty()) {
            hideLabel(lblErrorEmail);
            inputEmail.setStyle(defaultStyle());
        } else if (valid) {
            hideLabel(lblErrorEmail);
            inputEmail.setStyle(okStyle());
        } else {
            showLabel(lblErrorEmail);
            inputEmail.setStyle(errStyle());
        }
        return valid;
    }

    // ── Validasi password ───────────────────────────────────
    private boolean validatePassword(String val) {
        if (val.isEmpty()) {
            hideLabel(lblErrorPassword);
            return false;
        }
        if (val.length() < 8) {
            showLabel(lblErrorPassword);
            inputPassword.setStyle(errStyle());
            inputPasswordVisible.setStyle(errStyle());
            return false;
        }
        hideLabel(lblErrorPassword);
        inputPassword.setStyle(okStyle());
        inputPasswordVisible.setStyle(okStyle());
        return true;
    }

    // ── Validasi konfirmasi password ────────────────────────
    private boolean validatePassword2(String val) {
        String pw1 = pw1Visible ? inputPasswordVisible.getText() : inputPassword.getText();
        if (val.isEmpty()) {
            hideLabel(lblErrorPassword2);
            return false;
        }
        if (!val.equals(pw1)) {
            showLabel(lblErrorPassword2);
            inputPassword2.setStyle(errStyle());
            inputPassword2Visible.setStyle(errStyle());
            return false;
        }
        hideLabel(lblErrorPassword2);
        inputPassword2.setStyle(okStyle());
        inputPassword2Visible.setStyle(okStyle());
        return true;
    }

    // ── Indikator kekuatan password ─────────────────────────
    private void updatePasswordStrength(String pw) {
        int score = 0;
        if (pw.length() >= 8)              score++;
        if (pw.matches(".*[A-Z].*"))       score++;
        if (pw.matches(".*[0-9].*"))       score++;
        if (pw.matches(".*[^A-Za-z0-9].*")) score++;

        double progress = score / 4.0;
        passwordStrengthBar.setProgress(progress);

        String color, label;
        switch (score) {
            case 0 -> { color = "#e5e5e5"; label = ""; }
            case 1 -> { color = "#E74C3C"; label = "Lemah"; }
            case 2 -> { color = "#E67E22"; label = "Cukup"; }
            case 3 -> { color = "#27AE60"; label = "Kuat"; }
            default -> { color = "#1A6B3C"; label = "Sangat kuat"; }
        }

        passwordStrengthBar.setStyle("-fx-accent: " + color + ";");
        lblPasswordStrength.setText(label);
        lblPasswordStrength.setStyle("-fx-font-size: 10px; -fx-text-fill: " + color + ";");
    }

    // ── Handle submit register ──────────────────────────────
    @FXML
    private void handleRegisterSubmit() {
        String nama  = inputNama.getText().trim();
        String email = inputEmail.getText().trim();
        String pw    = pw1Visible ? inputPasswordVisible.getText() : inputPassword.getText();
        String pw2   = pw2Visible ? inputPassword2Visible.getText() : inputPassword2.getText();

        if (nama.isEmpty())          { showAlert("Nama lengkap tidak boleh kosong!"); return; }
        if (!validateEmail(email))   return;
        if (!validatePassword(pw))   return;
        if (!validatePassword2(pw2)) return;

        // TODO: panggil ApiService.register(nama, email, pw)
        System.out.println("Register: " + nama + " | " + email);
        showAlert("Akun berhasil dibuat! Silakan masuk 🎉");
        goToLogin();
    }

    // ── Navigasi ────────────────────────────────────────────
    @FXML
    private void goToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/view/Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnDaftar.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToRegister() { /* sudah di halaman ini */ }

    // ── Helper ──────────────────────────────────────────────
    private void showLabel(Label l) { l.setVisible(true); l.setManaged(true); }
    private void hideLabel(Label l) { l.setVisible(false); l.setManaged(false); }

    private String defaultStyle() {
        return "-fx-background-color: #F9F9F9; -fx-border-color: #E6E6E6;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-font-size: 13px; -fx-padding: 8 11 8 11;";
    }
    private String okStyle() {
        return "-fx-background-color: #F5FFF8; -fx-border-color: #27AE60;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-font-size: 13px; -fx-padding: 8 11 8 11;";
    }
    private String errStyle() {
        return "-fx-background-color: #FCEBEB; -fx-border-color: #C0392B;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-font-size: 13px; -fx-padding: 8 11 8 11;";
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}