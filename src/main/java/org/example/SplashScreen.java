package org.example;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class SplashScreen {

    public static void show(Stage primaryStage, Main mainApp) {
        Stage splashStage = new Stage(StageStyle.UNDECORATED);

        Image bgImage = null;
        try {
            bgImage = new Image(SplashScreen.class.getResource("/images/tetris.jpg").toExternalForm());
        } catch (Exception e) {
            System.out.println("Failed to load image: " + e.getMessage());
        }

        if (bgImage == null || bgImage.getWidth() == 0) {
            System.out.println("Image failed to load or has zero size.");
            bgImage = null;
        } else {
            System.out.println("Image loaded successfully: " + bgImage.getWidth() + " x " + bgImage.getHeight());
        }

        ImageView backgroundImageView = new ImageView(bgImage);
        if (bgImage != null) {
            backgroundImageView.setFitWidth(bgImage.getWidth());
            backgroundImageView.setFitHeight(bgImage.getHeight());
            backgroundImageView.setPreserveRatio(true);
        }

        Label label = new Label("Team: XYZ\nClass: 2006ICT\nProject: Tetris");
        label.setTextFill(Color.WHITE);
        label.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);

        StackPane root;
        if (bgImage != null) {
            root = new StackPane(backgroundImageView, label);
            root.setPrefSize(bgImage.getWidth(), bgImage.getHeight());
        } else {
            root = new StackPane(label);
            root.setStyle("-fx-background-color: black;");
            root.setPrefSize(600, 400);
        }

        Scene scene = new Scene(root, root.getPrefWidth(), root.getPrefHeight());

        splashStage.setScene(scene);
        splashStage.centerOnScreen();
        splashStage.show();

        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // Simulate some work (e.g., 3 seconds)
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
