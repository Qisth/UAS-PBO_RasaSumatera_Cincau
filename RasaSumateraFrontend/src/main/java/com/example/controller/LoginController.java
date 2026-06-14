package com.example.controller;

import com.example.service.ApiService;
import com.example.util.SessionManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * LoginController — menangani logika halaman Login
 * FXML : login.fxml
 */
public class LoginController implements Initializable {

    // ── Field FXML ──────────────────────────────────────────
    @FXML private TextField     inputEmail;
    @FXML private PasswordField inputPassword;
    @FXML private TextField     inputPasswordVisible;   // untuk toggle show/hide
    @FXML private Button        btnTogglePassword;
    @FXML private Label         lblErrorEmail;
    @FXML private Button        btnMasuk;

    private boolean passwordVisible = false;

    // ── Inisialisasi ────────────────────────────────────────
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Sembunyikan error label saat awal
        lblErrorEmail.setVisible(false);
        lblErrorEmail.setManaged(false);

        // Sync isi TextField visible ↔ PasswordField saat user mengetik
        inputPassword.textProperty().addListener((obs, old, val) -> {
            if (!passwordVisible) inputPasswordVisible.setText(val);
        });
        inputPasswordVisible.textProperty().addListener((obs, old, val) -> {
            if (passwordVisible) inputPassword.setText(val);
        });

        // Validasi email real-time
        inputEmail.textProperty().addListener((obs, old, val) -> validateEmail(val));
    }

    // ── Toggle show/hide password ───────────────────────────
    @FXML
    private void togglePassword() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            inputPasswordVisible.setText(inputPassword.getText());
            inputPasswordVisible.setVisible(true);
            inputPasswordVisible.setManaged(true);
            inputPassword.setVisible(false);
            inputPassword.setManaged(false);
            btnTogglePassword.setText("🙈");
        } else {
            inputPassword.setText(inputPasswordVisible.getText());
            inputPassword.setVisible(true);
            inputPassword.setManaged(true);
            inputPasswordVisible.setVisible(false);
            inputPasswordVisible.setManaged(false);
            btnTogglePassword.setText("👁");
        }
    }

    // ── Validasi email ──────────────────────────────────────
    private boolean validateEmail(String val) {
        boolean valid = val.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
        if (val.isEmpty()) {
            lblErrorEmail.setVisible(false);
            lblErrorEmail.setManaged(false);
            inputEmail.setStyle(getDefaultFieldStyle());
        } else if (valid) {
            lblErrorEmail.setVisible(false);
            lblErrorEmail.setManaged(false);
            inputEmail.setStyle(getOkFieldStyle());
        } else {
            lblErrorEmail.setVisible(true);
            lblErrorEmail.setManaged(true);
            inputEmail.setStyle(getErrFieldStyle());
        }
        return valid;
    }

    // ── Handle submit login ─────────────────────────────────
    @FXML
    private void handleLoginSubmit(ActionEvent event) {
        String email    = inputEmail.getText().trim();
        String password = passwordVisible
                ? inputPasswordVisible.getText()
                : inputPassword.getText();

        if (!validateEmail(email)) return;
        if (password.isEmpty()) {
            showAlert("Password tidak boleh kosong!");
            return;
        }

        try {
            // Memanggil ApiService untuk melakukan request autentikasi ke backend
            ApiService apiService = new ApiService();
            boolean loginSukses = apiService.login(email, password);

            if (loginSukses) {
                System.out.println("Login Berhasil untuk: " + email);
                showAlert("Login berhasil! Selamat datang " + SessionManager.getUsername() + " 🎉");

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.close();
            }
        } catch (RuntimeException e) {
            // Menangkap error response dari backend (misal: "Email salah" atau "Password tidak sesuai")
            showAlert(e.getMessage());
        } catch (Exception e) {
            // Menangkap error jika server Spring Boot tidak merespon/mati
            System.err.println("Koneksi gagal: " + e.getMessage());
            showAlert("Gagal terhubung ke server! Pastikan Backend Spring Boot sudah menyala.");
        }
    }

    // ── Navigasi ke Register ────────────────────────────────
    @FXML
    private void goToRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/view/Register.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnMasuk.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToLogin() { /* sudah di halaman ini */ }

    @FXML
    private void handleForgotPassword() {
        showAlert("Fitur reset password belum tersedia.");
    }

    // ── Navigasi ke halaman utama (setelah login sukses) ───
    private void navigateToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/view/main-view.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnMasuk.getScene().getWindow();
            stage.setScene(new Scene(root, 1100, 720));
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ── Helper style ────────────────────────────────────────
    private String getDefaultFieldStyle() {
        return "-fx-background-color: #F9F9F9; -fx-border-color: #E6E6E6;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-font-size: 13px; -fx-padding: 8 11 8 11;";
    }
    private String getOkFieldStyle() {
        return "-fx-background-color: #F5FFF8; -fx-border-color: #27AE60;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-font-size: 13px; -fx-padding: 8 11 8 11;";
    }
    private String getErrFieldStyle() {
        return "-fx-background-color: #FCEBEB; -fx-border-color: #C0392B;" +
                "-fx-border-radius: 8; -fx-background-radius: 8;" +
                "-fx-font-size: 13px; -fx-padding: 8 11 8 11;";
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, msg, ButtonType.OK);
        alert.showAndWait();
    }
}