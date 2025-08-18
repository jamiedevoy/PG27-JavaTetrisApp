package org.example.controllers;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import org.example.Main;

import java.util.Objects;

public class SplashController {
    @FXML private ImageView SplashImage;
    @FXML private Label SplashLabel;

    private Stage stage;
    private Main mainApp;

    public void initialize() {
        // Try to load splash screen image
        try {
            Image splashImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/images/tetris.jpg")));
            SplashImage.setImage(splashImage);
            SplashImage.setPreserveRatio(true);
            SplashImage.setFitHeight(splashImage.getHeight());
            SplashImage.setFitWidth(splashImage.getWidth());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void startSplash(Stage splashStage, Main mainApp) {
        this.stage = splashStage;
        this.mainApp = mainApp;

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(5000);
                return null;
            }

            @Override
            protected void succeeded() {
                Platform.runLater(() -> {
                    splashStage.close();
                    mainApp.showMainMenu();
                });
            }
        };

        new Thread(loadTask).start();
    }
}