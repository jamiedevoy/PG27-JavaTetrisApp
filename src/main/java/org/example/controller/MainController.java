package org.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.ConfigScreen;
import org.example.GameScreen;
import org.example.HighScoreScreen;
import org.example.Main;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.application.Platform;


public class MainController {

    @FXML private Button playButton;
    @FXML private Button configButton;
    @FXML private Button highScoresButton;
    @FXML private Button exitButton;

    private Stage primaryStage;
    private Main mainApp;


    public void initialize() {
        playButton.setOnAction(e -> GameScreen.show(primaryStage, mainApp::showMainMenu));
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


    @FXML
    protected void onPlayButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/Game.fxml"));
            Stage mainStage = new Stage(fxmlLoader.getController());
            mainStage.initStyle(StageStyle.DECORATED);
            //mainStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(fxmlLoader.load());
            mainStage.setScene(scene);
            GameScreen.show(mainStage, mainApp::showMainMenu);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
