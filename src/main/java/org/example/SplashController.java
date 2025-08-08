package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

public class SplashController {
    @FXML
    private ImageView SplashImage;

    @FXML
    public void initialize() {
        // Try to load splash screen image
        SplashImage.setImage(new Image(getClass().getResource("/images/tetris.jpg").toExternalForm()));

        PauseTransition delay = new PauseTransition(Duration.seconds(3));
        delay.setOnFinished(event -> loadMainMenu());
        delay.play();
    }

    private void loadMainMenu() {
        try {
            // Load Main Menu FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Main.fxml"));
            Scene mainMenuScene = new Scene(loader.load(), 600, 400);

            // Close splash screen
            Stage SplashStage = (Stage) SplashImage.getScene().getWindow();
            SplashStage.close();

            //Create a new decorated screen
            Stage mainStage = new Stage();
            mainStage.setTitle("Tetris 27");
            mainStage.setScene(mainMenuScene);
            mainStage.show();

        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
}