package org.example.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.interfaces.IScreen;

import java.io.IOException;

public class GameOverScreen implements IScreen {

    public void show(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent overlayRoot = loader.load();

            Stage overlayStage = new Stage();
            overlayStage.initOwner(primaryStage);
            overlayStage.setAlwaysOnTop(true);

            Scene scene = new Scene(overlayRoot);
            overlayStage.setScene(scene);
            overlayStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void show(Stage primaryStage, Runnable mainApp) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent overlayRoot = loader.load();

            Stage overlayStage = new Stage();
            overlayStage.initOwner(primaryStage);
            overlayStage.setAlwaysOnTop(true);

            Scene scene = new Scene(overlayRoot);
            overlayStage.setScene(scene);
            overlayStage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void hide(Stage primaryStage, Runnable mainApp) {

    }
}
