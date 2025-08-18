package org.example;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.controllers.SplashController;

public class SplashScreen {

    public static void show(Main mainApp) {
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
