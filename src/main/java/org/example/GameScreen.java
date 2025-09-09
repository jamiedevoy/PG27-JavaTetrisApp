package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controllers.GameScreenController;

public class GameScreen {

    // Keep your old signature for backwards compatibility
    public static void show(Stage primaryStage, Runnable mainApp, String playerName) {
        show(primaryStage, mainApp, playerName, false); // default single-player
    }

    // NEW overload: supports two-player flag
    public static void show(Stage primaryStage, Runnable mainApp, String playerName, boolean twoPlayer) {
        try {
            FXMLLoader loader = new FXMLLoader(GameScreen.class.getResource("/fxml/Game.fxml"));
            Parent root = loader.load();

            GameScreenController controller = loader.getController();
            controller.start(primaryStage, mainApp, playerName, twoPlayer);

            Scene scene = new Scene(root);
            primaryStage.setTitle(twoPlayer ? "Tetris - Two Player" : "Tetris");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

