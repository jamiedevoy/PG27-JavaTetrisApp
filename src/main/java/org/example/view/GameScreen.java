package org.example.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controllers.GameScreenController;

public class GameScreen {

    // Keep your old signature for backwards compatibility
    public static void show(Stage primaryStage, Runnable mainApp, String playerName) {
        show(primaryStage, mainApp, playerName, null, false); // default single-player
    }

    // NEW overload: supports two-player flag
    public static void show(Stage primaryStage, Runnable mainApp, String p1Name, String p2Name, boolean twoPlayer) {
        try {
            FXMLLoader loader = new FXMLLoader(GameScreen.class.getResource("/fxml/Game.fxml"));
            Parent root = loader.load();

            GameScreenController controller = loader.getController();
            controller.start(primaryStage, mainApp, p1Name, p2Name, twoPlayer);

            Scene scene = new Scene(root);
            primaryStage.setTitle(twoPlayer ? "Tetris - Two Player" : "Tetris");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void hide(Stage primaryStage) {

    }
}

