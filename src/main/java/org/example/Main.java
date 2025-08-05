package org.example;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {

    private Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        SplashScreen.show(primaryStage, this::showMainMenu);
    }

    private void showMainMenu() {
        Button playButton = new Button("Play");
        Button configButton = new Button("Configuration");
        Button highScoresButton = new Button("High Scores");
        Button exitButton = new Button("Exit");

        playButton.setPrefWidth(200);
        configButton.setPrefWidth(200);
        highScoresButton.setPrefWidth(200);
        exitButton.setPrefWidth(200);

        playButton.setOnAction(e -> GameScreen.show(primaryStage, this::showMainMenu));

        configButton.setOnAction(e -> ConfigScreen.show(primaryStage, this::showMainMenu));

        highScoresButton.setOnAction(e -> HighScoreScreen.show(primaryStage, this::showMainMenu));

        exitButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to exit?");

            // Show dialog and wait for response
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Platform.exit();
                } else {
                    // Do nothing, stays on main menu
                }
            });
        });


        VBox menuLayout = new VBox(20, playButton, configButton, highScoresButton, exitButton);
        menuLayout.setAlignment(Pos.CENTER);
        menuLayout.setStyle("-fx-background-color: light blue;");

        Scene menuScene = new Scene(menuLayout, 400, 300);
        primaryStage.setTitle("Tetris Main Menu");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }
}
