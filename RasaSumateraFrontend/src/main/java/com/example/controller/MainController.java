package com.example.controller;

import com.example.model.Daerah;
import com.example.model.Kuliner;
import com.example.service.ApiService;
import com.example.util.SessionManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML private Label lblUsername;
    @FXML private Button btnAuthNav;
    @FXML private Button btnAdmin;
    @FXML private Circle circleHeroImg;

    // Tombol filter daerah
    @FXML private Button btnDaerahAceh;
    @FXML private Button btnDaerahSumut;
    @FXML private Button btnDaerahSumbar;
    @FXML private Button btnDaerahLainnya;

    // Kartu kuliner favorit
    @FXML private VBox cardMieAceh;
    @FXML private VBox cardRendang;
    @FXML private VBox cardBikaAmbon;

    private final ApiService apiService = new ApiService();

    @FXML
    public void initialize() {
        refreshUserInfo();
    }

    private void refreshUserInfo() {
        if (SessionManager.getToken() != null && !SessionManager.getToken().isEmpty()) {

            lblUsername.setText("Halo, " + SessionManager.getUsername() + "!");
            btnAuthNav.setText("Logout");
            btnAuthNav.setStyle(
                    "-fx-background-color: #A32D2D; -fx-background-radius: 20; -fx-padding: 6 18;"
            );
            if (SessionManager.getUsername().equals("Admin")) {
                btnAdmin.setVisible(true);
                btnAdmin.setManaged(true);
            }

        } else {

            lblUsername.setText("");
            btnAuthNav.setText("Login");
            btnAuthNav.setStyle(
                    "-fx-background-color: #A32D2D; -fx-background-radius: 20; -fx-padding: 6 18;"
            );
        }
    }

    @FXML
    void handleAuthAction() throws Exception {
        if (btnAuthNav.getText().equals("Logout")) {
            // JALUR LOGOUT

            apiService.logout();
            SessionManager.clearSession();
            refreshUserInfo();
            System.out.println("User telah logout.");
            goToLogin();
        } else {
            goToLogin();
        }
    }

    void goToLogin() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/login.fxml"));

            Stage loginStage = new Stage();
            loginStage.initModality(Modality.APPLICATION_MODAL);
            loginStage.setTitle("Login - RasaSumatera");
            loginStage.setScene(new Scene(loader.load()));
            loginStage.setResizable(false);
            loginStage.showAndWait();

            refreshUserInfo();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdminPage(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/example/view/admin-view.fxml")
        );

        Scene scene = new Scene(loader.load());
        Stage adminStage = new Stage();
        adminStage.initOwner(
                ((Node) event.getSource())
                        .getScene()
                        .getWindow()
        );

        adminStage.setTitle("Kelola Kuliner");
        adminStage.setScene(scene);

        adminStage.initModality(Modality.APPLICATION_MODAL);
        adminStage.showAndWait();
    }

    /**
     * Dipanggil saat tombol daerah (Aceh, Sumut, Sumbar) diklik.
     * Akan membuka halaman Daftar Kuliner dengan filter daerah otomatis
     * berdasarkan nama daerah pada tombol yang ditekan.
     */
    @FXML
    void handleDaerahClick(ActionEvent event) {
        Button source = (Button) event.getSource();
        String namaDaerah = source.getText();

        openKulinerListWithFilter(namaDaerah, event);
    }

    /**
     * Tombol "..." -> membuka halaman Daftar Kuliner tanpa filter (tampil semua).
     */
    @FXML
    void handleLihatSemuaKuliner(ActionEvent event) {
        openKulinerList(null, event);
    }

    /**
     * Klik pada kartu Kuliner Favorit -> membuka halaman profil/detail kuliner
     * dengan mencari ID kuliner berdasarkan nama yang tercantum pada kartu.
     */
    @FXML
    void handleFavoriteCardClick(MouseEvent event) {
        VBox source = (VBox) event.getSource();
        String namaKuliner;

        if (source == cardMieAceh) {
            namaKuliner = "Mie Aceh";
        } else if (source == cardRendang) {
            namaKuliner = "Rendang Daging";
        } else if (source == cardBikaAmbon) {
            namaKuliner = "Bika Ambon";
        } else {
            return;
        }

        openKulinerDetailByName(namaKuliner, event);
    }

    /**
     * Mencari ID daerah berdasarkan nama, lalu membuka halaman Daftar Kuliner
     * dengan ComboBox filter daerah otomatis terisi sesuai daerah tersebut.
     */
    private void openKulinerListWithFilter(String namaDaerah, ActionEvent event) {
        new Thread(() -> {
            try {
                List<Daerah> listDaerah = apiService.getAllDaerah();
                Daerah target = null;
                for (Daerah d : listDaerah) {
                    if (d.getNama().equalsIgnoreCase(namaDaerah)) {
                        target = d;
                        break;
                    }
                }
                final Daerah finalTarget = target;
                Platform.runLater(() -> openKulinerList(finalTarget, event));
            } catch (Exception e) {
                Platform.runLater(() -> openKulinerList(null, event));
            }
        }).start();
    }

    /**
     * Membuka halaman Daftar Kuliner. Jika daerahAwal tidak null,
     * filter daerah pada halaman tersebut akan diatur otomatis.
     */
    private void openKulinerList(Daerah daerahAwal, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/kuliner-list-view.fxml"));
            Parent root = loader.load();

            if (daerahAwal != null) {
                KulinerListController controller = loader.getController();
                controller.setDaerahAwal(daerahAwal);
            }

            navigateTo(root, event);
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Error", "Gagal membuka halaman Daftar Kuliner.");
        }
    }

    /**
     * Mencari kuliner berdasarkan nama, lalu membuka halaman profil/detailnya.
     */
    private void openKulinerDetailByName(String nama, Event event) {
        new Thread(() -> {
            try {
                List<Kuliner> hasil = apiService.searchKuliner(nama);
                if (hasil.isEmpty()) {
                    Platform.runLater(() -> showAlert(AlertType.WARNING, "Tidak Ditemukan", "Data kuliner \"" + nama + "\" tidak ditemukan."));
                    return;
                }

                Long id = hasil.get(0).getId();
                Platform.runLater(() -> openKulinerDetail(id, event));
            } catch (Exception e) {
                Platform.runLater(() -> showAlert(AlertType.ERROR, "Error", "Gagal memuat data kuliner. Pastikan server backend berjalan."));
            }
        }).start();
    }

    /**
     * Membuka halaman profil/detail kuliner berdasarkan ID.
     */
    private void openKulinerDetail(Long id, Event event) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/view/kuliner-detail-view.fxml")
            );

            Parent root = loader.load();

            KulinerDetailController controller = loader.getController();
            controller.setKulinerId(id);

            navigateTo(root, event); // atau node lain yang pasti ada

        } catch (IOException e) {
            showAlert(AlertType.ERROR,
                    "Error",
                    "Gagal membuka halaman profil kuliner.");
        }
    }

    /**
     * Mengganti isi Scene saat ini dengan halaman (root node) yang baru.
     */
    private void navigateTo(Parent root, Event event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        Scene scene = stage.getScene();
        scene.setRoot(root);
    }

    private void showAlert(AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
