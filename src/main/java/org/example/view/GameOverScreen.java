package org.example.view;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.controllers.GameScreenController;
import org.example.interfaces.IScreen;
import org.example.controllers.GOController;
import java.io.IOException;

public class GameOverScreen implements IScreen {

    public void show(Stage primaryStage, int score, int level, int linesCleared) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent overlayRoot = loader.load();

            GOController controller = loader.getController();
            controller.setStats(score, level, linesCleared);

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

    public Parent getOverlay(int score, int level, int linesCleared, GameScreenController gameScreenController) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/GameOver.fxml"));
            Parent overlayRoot = loader.load();

            GOController controller = loader.getController();
            controller.setStats(score, level, linesCleared);
            controller.initialize(gameScreenController);

            StackPane overlay = new StackPane(overlayRoot);
            overlay.setAlignment(Pos.CENTER);
            //overlay.prefWidthProperty().bind(controller.gameLayout.widthProperty());
            //overlay.prefHeightProperty().bind(controller.gameLayout.heightProperty());

            return overlay;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
