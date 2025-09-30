package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.view.ConfigScreen;
import org.example.view.GameScreen;
import org.example.view.HighScoreScreen;
import org.example.view.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;

import java.util.concurrent.atomic.AtomicReference;


public class MainController extends BaseController {

    @FXML public VBox menuLayout;
    @FXML private Button playButton;
    @FXML private Button configButton;
    @FXML private Button highScoresButton;
    @FXML private Button exitButton;

    private Stage primaryStage;
    private Main mainApp;


    public void initialize() {
        playButton.setOnAction(e -> {

            org.example.model.GameSettings gs = org.example.model.SettingsStore.getInstance().get();
            boolean twoPlayer = gs.twoPlayerEnabled();
            boolean p1AI = gs.aiPlayer1Enabled();
            boolean p2AI = gs.aiPlayer2Enabled();


            AtomicReference<String> p1Name = new AtomicReference<>();
            AtomicReference<String> p2Name = new AtomicReference<>();

            if (p1AI) {
                p1Name.set("Player 1 AI");
                if (twoPlayer && !p2AI) {
                    TextInputDialog dialog = new TextInputDialog("Player Two");
                    dialog.setTitle("Player Two Name");
                    dialog.setHeaderText("Please enter name.");
                    dialog.setContentText("Name:");
                    dialog.showAndWait().ifPresent(name -> {
                        if (!name.trim().isEmpty()) {
                            p2Name.set(name);
                        } else {
                            showErrorAlert("Player name cannot be empty.");
                        }
                    });
                } else if (twoPlayer && p2AI) {
                    p2Name.set("Player 2 AI");
                }
            }

            else {
                TextInputDialog dialogP1 = new TextInputDialog("Player One");
                dialogP1.setTitle("Player One Name");
                dialogP1.setHeaderText("Please enter your name.");
                dialogP1.setContentText("Name:");

                dialogP1.showAndWait().ifPresent(name -> {
                    if (!name.trim().isEmpty()) {
                        p1Name.set(name);

                        if (twoPlayer && !p2AI) {
                            TextInputDialog dialogP2 = new TextInputDialog("Player Two");
                            dialogP2.setTitle("Player Two Name");
                            dialogP2.setHeaderText("Please enter your name.");
                            dialogP2.setContentText("Name:");
                            dialogP2.showAndWait().ifPresent(p2 -> {
                                if (!p2.trim().isEmpty()) {
                                    p2Name.set(p2);
                                } else {
                                    showErrorAlert("Player Two name cannot be empty.");
                                }
                            });
                        } else if (twoPlayer && p2AI) {
                            p2Name.set("AI");
                        }
                    } else {
                        showErrorAlert("Player One name cannot be empty.");
                    }
                });
            }
            if (p1Name.get() != null && (!twoPlayer || p2Name.get() != null)) {
                org.example.audio.AudioManager.getInstance()
                        .configure(gs.musicEnabled(), gs.soundEffectsEnabled());

                org.example.audio.AudioManager.getInstance()
                        .playMusic("/audio/theme.mp3");

                GameScreen.show(primaryStage, mainApp::showMainMenu, p1Name.get(), p2Name.get(), twoPlayer);
            }
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

    private void showErrorAlert(String message) {
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(message);
        errorAlert.showAndWait();
    }

    public void setMainApp(Main mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
    }
}
