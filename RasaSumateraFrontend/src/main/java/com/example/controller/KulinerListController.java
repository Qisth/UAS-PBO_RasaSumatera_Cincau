package com.example.controller;

import com.example.model.Daerah;
import com.example.model.Kuliner;
import com.example.service.ApiService;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class KulinerListController {

    @FXML private TextField inputSearch;
    @FXML private ComboBox<Daerah> comboDaerah;
    @FXML private Button btnResetFilter;
    @FXML private FlowPane flowKuliner;
    @FXML private Label lblStatus;

    private final ApiService apiService = new ApiService();

    /**
     * Daerah yang dipilih dari halaman Home (lewat tombol "Jelajahi per daerah").
     * Jika null, berarti halaman dibuka tanpa filter awal (tampil semua kuliner).
     */
    private Daerah daerahAwal;

    @FXML
    public void initialize() {
        loadDaerah();
        loadKuliner();

        // Listener: setiap kali combo box daerah berubah -> filter ulang
        comboDaerah.valueProperty().addListener((obs, oldVal, newVal) -> applyFilter());

        // Listener: setiap kali teks pencarian berubah -> filter ulang (live search)
        inputSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilter());
    }

    /**
     * Dipanggil dari MainController saat user menekan tombol daerah di Home.
     * Akan langsung mengatur ComboBox filter daerah ke daerah yang dipilih.
     */
    public void setDaerahAwal(Daerah daerah) {
        this.daerahAwal = daerah;
        if (comboDaerah.getItems().contains(daerah)) {
            comboDaerah.setValue(daerah);
        } else {
            // Daerah belum termuat (race condition), tunggu lalu set
            Platform.runLater(() -> {
                if (comboDaerah.getItems().contains(daerah)) {
                    comboDaerah.setValue(daerah);
                }
            });
        }
    }

    /**
     * Memuat daftar daerah dari backend untuk mengisi ComboBox filter.
     */
    private void loadDaerah() {
        new Thread(() -> {
            try {
                List<Daerah> listDaerah = apiService.getAllDaerah();
                Platform.runLater(() -> {
                    comboDaerah.getItems().clear();
                    comboDaerah.getItems().addAll(listDaerah);

                    if (daerahAwal != null) {
                        for (Daerah d : listDaerah) {
                            if (d.getId().equals(daerahAwal.getId())) {
                                comboDaerah.setValue(d);
                                break;
                            }
                        }
                    }
                });
            } catch (Exception e) {
                Platform.runLater(() -> lblStatus.setText("Gagal memuat daftar daerah: " + e.getMessage()));
            }
        }).start();
    }

    /**
     * Memuat seluruh data kuliner dari backend dan menampilkannya sebagai kartu.
     */
    private void loadKuliner() {
        lblStatus.setText("Memuat data kuliner...");
        new Thread(() -> {
            try {
                List<Kuliner> listKuliner = apiService.getAllKuliner();
                Platform.runLater(() -> renderKuliner(listKuliner));
            } catch (Exception e) {
                Platform.runLater(() -> lblStatus.setText("Gagal memuat data kuliner. Pastikan server backend berjalan."));
            }
        }).start();
    }

    /**
     * Menggabungkan filter pencarian (nama) dan filter daerah (ComboBox),
     * lalu mengambil data terbaru dari backend sesuai kombinasi filter tersebut.
     */
    private void applyFilter() {
        String keyword = inputSearch.getText() == null ? "" : inputSearch.getText().trim();
        Daerah selectedDaerah = comboDaerah.getValue();

        lblStatus.setText("Memuat data kuliner...");

        new Thread(() -> {
            try {
                List<Kuliner> hasil;

                if (selectedDaerah != null) {
                    // Filter berdasarkan daerah dulu, lalu filter nama secara lokal jika ada keyword
                    hasil = apiService.getKulinerByDaerah(selectedDaerah.getId());
                    if (!keyword.isEmpty()) {
                        hasil = hasil.stream()
                                .filter(k -> k.getNama().toLowerCase().contains(keyword.toLowerCase()))
                                .toList();
                    }
                } else if (!keyword.isEmpty()) {
                    hasil = apiService.searchKuliner(keyword);
                } else {
                    hasil = apiService.getAllKuliner();
                }

                List<Kuliner> finalHasil = hasil;
                Platform.runLater(() -> renderKuliner(finalHasil));
            } catch (Exception e) {
                Platform.runLater(() -> lblStatus.setText("Gagal memuat data kuliner. Pastikan server backend berjalan."));
            }
        }).start();
    }

    /**
     * Menampilkan daftar kuliner sebagai kartu-kartu di dalam FlowPane.
     */
    private void renderKuliner(List<Kuliner> listKuliner) {
        flowKuliner.getChildren().clear();

        if (listKuliner.isEmpty()) {
            lblStatus.setText("Tidak ada kuliner yang ditemukan.");
            return;
        }

        lblStatus.setText(listKuliner.size() + " kuliner ditemukan");

        for (Kuliner k : listKuliner) {
            flowKuliner.getChildren().add(buildKulinerCard(k));
        }
    }

    /**
     * Membuat satu kartu kuliner yang dapat diklik untuk membuka halaman profil/detail.
     */
    private VBox buildKulinerCard(Kuliner kuliner) {
        VBox card = new VBox(4);
        card.setPrefWidth(135.0);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #E6E6E6; -fx-border-radius: 12; -fx-padding: 8; -fx-cursor: hand;");

        // 1. Membuat Container StackPane sebagai pembungkus gambar agar posisi otomatis di tengah
        StackPane thumbnailContainer = new StackPane();
        thumbnailContainer.setPrefHeight(80.0);
        thumbnailContainer.setPrefWidth(119.0); // Disesuaikan dengan lebar VBox setelah dikurangi padding
        thumbnailContainer.setMinSize(119.0, 80.0); // Mengunci ukuran minimum agar kontainer tidak menyusut
        thumbnailContainer.setMaxSize(119.0, 80.0); // Mengunci ukuran maksimum
        thumbnailContainer.setStyle("-fx-background-color: #FCEBEB; -fx-background-radius: 8;");

        // 2. Membuat objek ImageView untuk menampung gambar
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(false);
        imageView.setPickOnBounds(true);
        imageView.fitWidthProperty().bind(thumbnailContainer.widthProperty());
        imageView.fitHeightProperty().bind(thumbnailContainer.heightProperty());

        Rectangle clip = new Rectangle();
        clip.widthProperty().bind(thumbnailContainer.widthProperty());
        clip.heightProperty().bind(thumbnailContainer.heightProperty());
        clip.setArcWidth(16);  // Mengikuti kelengkungan border-radius kontainer
        clip.setArcHeight(16);
        thumbnailContainer.setClip(clip);

        // 3. Memuat gambar secara aman dari resources internal JavaFX
        try {
            // Lokasi file berada di: src/main/resources/com/example/images/
            String imagePath = "/com/example/images/" + kuliner.getimageUrl();

            var imageStream = getClass().getResourceAsStream(imagePath);
            if (imageStream != null) {
                Image image = new Image(imageStream);
                imageView.setImage(image);
            } else {
                // Jika file gambar spesifik tidak ditemukan, pakai gambar placeholder default
                Image defaultImage = new Image(getClass().getResourceAsStream("/com/example/images/default-placeholder.jpg"));
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            System.err.println("Gagal memuat gambar untuk " + kuliner.getNama() + ": " + e.getMessage());
        }

        // 4. Masukkan ImageView ke dalam container pembungkusnya
        thumbnailContainer.getChildren().add(imageView);

        // 5. Membuat label teks kartu
        Label namaLabel = new Label(kuliner.getNama());
        namaLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #791F1F;");
        namaLabel.setWrapText(true);

        Label daerahLabel = new Label("📍 " + (kuliner.getDaerah() != null ? kuliner.getDaerah() : "Tidak Diketahui"));
        daerahLabel.setStyle("-fx-text-fill: #555555;");

        // 6. Masukkan container gambar (StackPane) dan label ke dalam kartu utama (VBox)
        card.getChildren().addAll(thumbnailContainer, namaLabel, daerahLabel);

        // Klik kartu -> buka halaman profil/detail kuliner
        card.setOnMouseClicked(event -> openDetail(kuliner));

        return card;
    }

    /**
     * Membuka halaman profil/detail kuliner dan mengirimkan ID kuliner yang dipilih.
     */
    private void openDetail(Kuliner kuliner) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/kuliner-detail-view.fxml"));
            Parent root = loader.load();

            KulinerDetailController controller = loader.getController();
            controller.setKulinerId(kuliner.getId());

            navigateTo(root);
        } catch (IOException e) {
            lblStatus.setText("Gagal membuka halaman profil kuliner.");
        }
    }

    /**
     * Kembali ke halaman Beranda (main-view).
     */
    @FXML
    void handleKembali(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/view/main-view.fxml"));
            Parent root = loader.load();
            navigateTo(root);
        } catch (IOException e) {
            lblStatus.setText("Gagal kembali ke halaman utama.");
        }
    }

    /**
     * Mereset filter daerah dan pencarian, menampilkan kembali semua kuliner.
     */
    @FXML
    void handleResetFilter(ActionEvent event) {
        comboDaerah.setValue(null);
        inputSearch.clear();
        loadKuliner();
    }

    /**
     * Mengganti isi Scene saat ini dengan halaman (root node) yang baru.
     */
    private void navigateTo(Parent root) {
        Node source = flowKuliner;
        Stage stage = (Stage) source.getScene().getWindow();
        Scene scene = source.getScene();
        scene.setRoot(root);
        stage.setScene(scene);
    }
}
