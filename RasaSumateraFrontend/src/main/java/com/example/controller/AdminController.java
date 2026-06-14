package com.example.controller;

import com.example.dto.StatistikResponse;
import com.example.model.Daerah;
import com.example.model.Kuliner;
import com.example.service.ApiService;
import com.example.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AdminController {

    @FXML private Label lblGambar;

    @FXML private TableView<Kuliner> tableKuliner;
    @FXML private TableColumn<Kuliner, String> colNama;
    @FXML private TableColumn<Kuliner, String> colDaerah;
    @FXML private TableColumn<Kuliner, String> colDeskripsi;
    @FXML private TableColumn<Kuliner, String> colimageUrl;

    @FXML private TextField inputNamaKuliner;
    @FXML private ComboBox<Daerah> comboDaerah;
    @FXML private TextArea inputDeskripsi;
    @FXML private Button btnSimpan;
    @FXML private Button btnHapus;

    @FXML private Label lblTotalKuliner;
    @FXML private Label lblTotalDaerah;
    @FXML private Label lblTotalUlasan;

    private final ApiService apiService = new ApiService();

    // List untuk menampung data di tabel (Observable agar UI auto-update)
    private ObservableList<Kuliner> listKuliner = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        initTable();
        loadDaerah();
        loadKuliner();
        refreshStatistics();
    }

    private void initTable() {
        colNama.setCellValueFactory(new PropertyValueFactory<>("nama"));
        colDaerah.setCellValueFactory(new PropertyValueFactory<>("daerah"));
        colDeskripsi.setCellValueFactory(new PropertyValueFactory<>("deskripsi"));
        colimageUrl.setCellValueFactory(new PropertyValueFactory<>("imageUrl"));

        tableKuliner.setItems(listKuliner);
    }

    private void loadDaerah() {
        try {
            List<Daerah> daerahList = apiService.getAllDaerah();
            comboDaerah.setItems(
                    FXCollections.observableArrayList(daerahList)
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleTambahDaerah(ActionEvent event) {

        TextInputDialog dialog = new TextInputDialog();

        dialog.setTitle("Tambah Daerah");
        dialog.setHeaderText(null);
        dialog.setContentText("Nama daerah:");

        java.util.Optional<String> result = dialog.showAndWait();

        result.ifPresent(namaDaerah -> {

            try {

                apiService.addDaerah(namaDaerah);
                loadDaerah();

            } catch (Exception e) {

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setContentText("Gagal menambah daerah");
                alert.show();

            }
        });
    }

    @FXML
    void handlePilihGambar(ActionEvent event) {

        FileChooser chooser = new FileChooser();

        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(
                        "Images",
                        "*.jpg",
                        "*.jpeg",
                        "*.png"
                )
        );

        File file = chooser.showOpenDialog(
                btnSimpan.getScene().getWindow()
        );

        if (file != null) {

            lblGambar.setText(file.getName());

        }
    }

    private void loadKuliner() {
        try {
            List<Kuliner> data = apiService.getAllKuliner();
            listKuliner.setAll(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void handleSimpan(ActionEvent event) {
        String nama = inputNamaKuliner.getText();
        Daerah daerah = comboDaerah.getValue();
        String deskripsi = inputDeskripsi.getText();

        if (nama.isBlank() || daerah == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setContentText("Nama dan daerah wajib diisi");
            alert.show();
            return;
        }

        Kuliner kuliner = new Kuliner(nama, daerah.toString(), deskripsi, lblGambar.getText());
        kuliner.setNama(nama);
        kuliner.setDeskripsi(deskripsi);
        kuliner.setImageUrl(lblGambar.getText());

        try {
            apiService.addKuliner(kuliner, daerah.getId());

            loadKuliner();
            resetForm();
            refreshStatistics();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetForm() {
        inputNamaKuliner.clear();
        inputDeskripsi.clear();
        comboDaerah.getSelectionModel().clearSelection();
        lblGambar.setText("None");
    }

    @FXML
    void handleHapus(ActionEvent event) {
        Kuliner selected = tableKuliner.getSelectionModel().getSelectedItem();

        if(selected == null) {

            new Alert(
                    Alert.AlertType.WARNING,
                    "Pilih kuliner terlebih dahulu"
            ).show();

            return;
        }

        try {

            apiService.deleteKuliner(selected.getId());

            loadKuliner();

            refreshStatistics();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void refreshStatistics() {

        try {

            StatistikResponse statistik = apiService.getStatistik();
            lblTotalKuliner.setText(String.valueOf(statistik.getTotalKuliner()));
            lblTotalDaerah.setText(String.valueOf(statistik.getTotalProvinsi()));
            lblTotalUlasan.setText(String.valueOf(statistik.getTotalUlasan()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}