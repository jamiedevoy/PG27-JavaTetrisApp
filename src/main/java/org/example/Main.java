package org.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

/* OG Code
public class Main extends Application {

    public Stage primaryStage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        SplashScreen.show(primaryStage, this);
    }

    // 06/08/25 - SC - Update showMainMenu() to public to cater for splashscreen passing
    public void showMainMenu() {
        Button playButton = new Button("Play");
        Button configButton = new Button("Configuration");
        Button highScoresButton = new Button("High Scores");
        Button exitButton = new Button("Exit");

        //06/8/25 - SC converted enhanced for loop as per https://lms.griffith.edu.au/courses/29105/files/7734485?wrap=1
        /*
        playButton.setPrefWidth(200);
        configButton.setPrefWidth(200);
        highScoresButton.setPrefWidth(200);
        exitButton.setPrefWidth(200);
         *//*
        Button[] buttons = { playButton, configButton, highScoresButton, exitButton };
        for (Button button : buttons) {
            button.setPrefWidth(200);
        }

        playButton.setOnAction(e -> GameScreen.show(primaryStage, this::showMainMenu));

        configButton.setOnAction(e -> ConfigScreen.show(primaryStage, this::showMainMenu));

        highScoresButton.setOnAction(e -> HighScoreScreen.show(primaryStage, this::showMainMenu));

        exitButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Exit Confirmation");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to exit?");

            // Show dialog and wait for response
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    Platform.exit();
                } else {
                    // Do nothing, stays on main menu
                }
            });
        });


        VBox menuLayout = new VBox(20, playButton, configButton, highScoresButton, exitButton);
        menuLayout.setAlignment(Pos.CENTER);
        //06/8/25 - SC - this was causing java.lang.ClassCastException due to colour defined. updated to corrected value
        menuLayout.setStyle("-fx-background-color: lightblue;");

        Scene menuScene = new Scene(menuLayout, 400, 300);
        primaryStage.setTitle("Tetris Main Menu");
        primaryStage.setScene(menuScene);
        primaryStage.show();
    }
}*/


/* Rhys Implementation */
public class Main extends Application {

    @Override
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
    }

    public static void main(String[] args) {
        launch(args);
    }
}