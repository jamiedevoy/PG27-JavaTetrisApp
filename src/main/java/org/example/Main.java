package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import org.example.controllers.MainController;


public class Main extends Application {

    public Stage primaryStage;

    /*@Override
    public void start(Stage primaryStage) {
        try {
            // Load Splash Screen FXML
            FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/Splash.fxml"));
            Stage splashStage = new Stage(fxmlLoader.getController());
            Scene scene = new Scene(fxmlLoader.load());
            splashStage.initStyle(StageStyle.TRANSPARENT);
            splashStage.initModality(Modality.WINDOW_MODAL);
            splashStage.setScene(scene);
            splashStage.show();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }*/

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        SplashScreen.show(this);
    }

    public void showMainMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Main.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.setMainApp(this, primaryStage);

            Scene scene = new Scene(root);
            primaryStage.setTitle("Tetris Main Menu");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}