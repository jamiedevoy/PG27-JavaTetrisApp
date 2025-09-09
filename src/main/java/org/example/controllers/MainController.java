package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import org.example.ConfigScreen;
import org.example.GameScreen;
import org.example.HighScoreScreen;
import org.example.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;


public class MainController extends BaseController {

    @FXML private Button playButton;
    @FXML private Button configButton;
    @FXML private Button highScoresButton;
    @FXML private Button exitButton;

    private Stage primaryStage;
    private Main mainApp;


    public void initialize() {
        playButton.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Player Name Required");
            dialog.setHeaderText("Please enter your name to start the game.");
            dialog.setContentText("Name:");
            dialog.setGraphic(null);

            dialog.showAndWait().ifPresent(playerName -> {
                if (!playerName.trim().isEmpty()) {
                    System.out.println("Player name entered: " + playerName);

                    // 👉 Read settings saved from Config (including Two Player)
                    org.example.model.GameSettings gs = org.example.model.SettingsStore.get();
                    boolean twoPlayer = gs.twoPlayerEnabled();

                    // 👉 Call the two-player-aware overload
                    GameScreen.show(primaryStage, mainApp::showMainMenu, playerName, twoPlayer);

                } else {
                    System.out.println("No player name was entered.");

                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Error");
                    errorAlert.setHeaderText(null);
                    errorAlert.setContentText("Player name cannot be empty.");
                    errorAlert.showAndWait();
                }
            });
        });

        configButton.setOnAction(e -> ConfigScreen.show(primaryStage, mainApp::showMainMenu));
        highScoresButton.setOnAction(e -> HighScoreScreen.show(primaryStage, mainApp::showMainMenu));

        exitButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to exit?");
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Platform.exit();
                }
            });
        });
    }

    public void setMainApp(Main mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
    }
}
