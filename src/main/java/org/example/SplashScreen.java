package org.example;

import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.controller.SplashController;

import java.io.IOException;

public class SplashScreen {

    public static void show(Stage owner, Main mainApp) throws IOException {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(SplashScreen.class.getResource("/fxml/Splash.fxml"));
            Parent root = fxmlLoader.load();

            SplashController controller = fxmlLoader.getController();
            Stage splashStage = new Stage(StageStyle.UNDECORATED);
            splashStage.setScene(new Scene(root));
            splashStage.centerOnScreen();
            splashStage.show();

            controller.startSplash(splashStage, mainApp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
