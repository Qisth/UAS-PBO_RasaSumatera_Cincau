package com.example.controller;

import javafx.animation.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainController {

    // =========================
    // COMPONENT YANG SUDAH ADA
    // =========================

    @FXML
    private Button btnLoginNav;

    @FXML
    private Button btnKirimUlasan;

    @FXML
    private TextArea inputUlasan;

    @FXML
    private Circle circleHeroImg;

    // =========================
    // COMPONENT UNTUK ANIMASI
    // =========================

    @FXML
    private AnchorPane heroPane;

    @FXML
    private ImageView imgHero;

    @FXML
    private VBox heroText;

    @FXML
    private HBox provinceSection;

    @FXML
    private FlowPane foodSection;

    @FXML
    private VBox testimonialSection;

    @FXML
    private Button btnDaftar;

    // =========================
    // INITIALIZE
    // =========================

    @FXML
    public void initialize() {

        playIntroAnimation();
        animateCards();
        setupButtonGlow();
    }

    // =========================
    // LOGIN
    // =========================

    @FXML
    void handleNavLogin(ActionEvent event) {

        try {

            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/view/login.fxml"
                    )
            );

            Stage loginStage = new Stage();

            loginStage.initModality(
                    Modality.APPLICATION_MODAL
            );

            loginStage.setTitle(
                    "Login - RasaSumatera"
            );

            loginStage.setScene(
                    new Scene(loader.load())
            );

            loginStage.setResizable(false);

            loginStage.showAndWait();

        } catch (IOException e) {

            e.printStackTrace();
        }
    }

    // =========================
    // ULASAN
    // =========================

    @FXML
    void handleKirimUlasan(ActionEvent event) {

        if (inputUlasan == null)
            return;

        String ulasanText =
                inputUlasan.getText().trim();

        if (ulasanText.isEmpty()) {

            showAlert(
                    AlertType.WARNING,
                    "Validasi Gagal",
                    "Ulasan tidak boleh kosong!"
            );

            return;
        }

        System.out.println(
                "Mengirim ulasan: "
                        + ulasanText
        );

        showAlert(
                AlertType.INFORMATION,
                "Sukses",
                "Ulasan kamu berhasil terkirim!"
        );

        inputUlasan.clear();
    }

    // =========================
    // INTRO ANIMATION
    // =========================

    private void playIntroAnimation() {

        if (heroPane == null)
            return;

        heroPane.setOpacity(0);

        if (imgHero != null) {
            imgHero.setTranslateX(-250);
        }

        if (heroText != null) {
            heroText.setTranslateX(250);
        }

        FadeTransition fade =
                new FadeTransition(
                        Duration.seconds(1.2),
                        heroPane
                );

        fade.setFromValue(0);
        fade.setToValue(1);

        TranslateTransition imageSlide =
                new TranslateTransition(
                        Duration.seconds(1),
                        imgHero
                );

        imageSlide.setToX(0);

        TranslateTransition textSlide =
                new TranslateTransition(
                        Duration.seconds(1),
                        heroText
                );

        textSlide.setToX(0);

        ParallelTransition intro =
                new ParallelTransition(
                        fade,
                        imageSlide,
                        textSlide
                );

        intro.play();
    }

    // =========================
    // CARD ANIMATION
    // =========================

    private void animateCards() {

        if (foodSection == null)
            return;

        ObservableList<Node> cards =
                foodSection.getChildren();

        for (int i = 0; i < cards.size(); i++) {

            Node card = cards.get(i);

            card.setOpacity(0);

            FadeTransition fade =
                    new FadeTransition(
                            Duration.millis(500),
                            card
                    );

            fade.setDelay(
                    Duration.millis(i * 200)
            );

            fade.setFromValue(0);
            fade.setToValue(1);

            fade.play();

            addHoverEffect(card);
        }
    }

    // =========================
    // HOVER EFFECT
    // =========================

    private void addHoverEffect(Node card) {

        card.setOnMouseEntered(e -> {

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(150),
                            card
                    );

            scale.setToX(1.05);
            scale.setToY(1.05);

            scale.play();
        });

        card.setOnMouseExited(e -> {

            ScaleTransition scale =
                    new ScaleTransition(
                            Duration.millis(150),
                            card
                    );

            scale.setToX(1);
            scale.setToY(1);

            scale.play();
        });
    }

    // =========================
    // CTA GLOW EFFECT
    // =========================

    private void setupButtonGlow() {

        if (btnDaftar == null)
            return;

        DropShadow shadow =
                new DropShadow();

        btnDaftar.setEffect(shadow);

        Timeline pulse =
                new Timeline(

                        new KeyFrame(
                                Duration.ZERO,
                                new KeyValue(
                                        shadow.radiusProperty(),
                                        5
                                )
                        ),

                        new KeyFrame(
                                Duration.seconds(1),
                                new KeyValue(
                                        shadow.radiusProperty(),
                                        25
                                )
                        )
                );

        pulse.setCycleCount(
                Animation.INDEFINITE
        );

        pulse.setAutoReverse(true);

        pulse.play();
    }

    // =========================
    // ALERT
    // =========================

    private void showAlert(
            AlertType type,
            String title,
            String content
    ) {

        Alert alert =
                new Alert(type);

        alert.setTitle(title);

        alert.setHeaderText(null);

        alert.setContentText(content);

        alert.showAndWait();
    }
}