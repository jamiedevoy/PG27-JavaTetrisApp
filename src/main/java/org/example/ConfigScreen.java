package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.controllers.ConfigController;

public class ConfigScreen {

    //FXML implementation
    public static void show(Stage primaryStage, Runnable mainApp) {
        try {
            FXMLLoader loader = new FXMLLoader(ConfigScreen.class.getResource("/fxml/Config.fxml"));
            System.out.println(ConfigScreen.class.getResource("/fxml/Config.fxml"));
            Parent root = loader.load();

            ConfigController controller = loader.getController();
            controller.start(primaryStage, mainApp);

            Scene scene = new Scene(root);
            primaryStage.setTitle("Config");
            primaryStage.setScene(scene);
            primaryStage.show();

            //root.requestFocus(); // Ensure key events are captured
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}