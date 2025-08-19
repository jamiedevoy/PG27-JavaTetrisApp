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
    /* Jamie Implementation
    public static void show(Stage primaryStage, Runnable onBack) {
        // Field Size controls
        Label fieldSizeLabel = new Label("Field Size:");
        Slider fieldSizeSlider = new Slider(10, 20, 10);
        fieldSizeSlider.setMajorTickUnit(2);
        fieldSizeSlider.setMinorTickCount(1);
        fieldSizeSlider.setSnapToTicks(true);
        fieldSizeSlider.setShowTickLabels(true);
        fieldSizeSlider.setShowTickMarks(true);
        fieldSizeSlider.setPrefWidth(300);
        Label fieldSizeValue = new Label(Integer.toString((int) fieldSizeSlider.getValue()));
        fieldSizeValue.setPadding(new Insets(0, 0, 10, 5)); // padding below slider

        fieldSizeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            fieldSizeValue.setText(Integer.toString(newVal.intValue()));
        });

        VBox fieldSizeBox = new VBox(5, fieldSizeLabel, fieldSizeSlider, fieldSizeValue);
        fieldSizeBox.setAlignment(Pos.TOP_LEFT);

        // Level controls
        Label levelLabel = new Label("Level:");
        Slider levelSlider = new Slider(1, 10, 1);
        levelSlider.setMajorTickUnit(1);
        levelSlider.setMinorTickCount(0);
        levelSlider.setSnapToTicks(true);
        levelSlider.setShowTickLabels(true);
        levelSlider.setShowTickMarks(true);
        levelSlider.setPrefWidth(300);
        Label levelValue = new Label(Integer.toString((int) levelSlider.getValue()));
        levelValue.setPadding(new Insets(0, 0, 10, 5));

        levelSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            levelValue.setText(Integer.toString(newVal.intValue()));
        });

        VBox levelBox = new VBox(5, levelLabel, levelSlider, levelValue);
        levelBox.setAlignment(Pos.TOP_LEFT);

        // Checkboxes
        CheckBox musicCheckBox = new CheckBox("Music");
        CheckBox soundEffectsCheckBox = new CheckBox("Sound Effects");
        CheckBox aiPlayCheckBox = new CheckBox("AI Play");
        CheckBox extendedModeCheckBox = new CheckBox("Extended Mode");

        VBox checkBoxGroup = new VBox(10, musicCheckBox, soundEffectsCheckBox, aiPlayCheckBox, extendedModeCheckBox);
        checkBoxGroup.setAlignment(Pos.TOP_LEFT);

        // Back button
        Button backButton = new Button("Back");
        backButton.setOnAction(e -> onBack.run());

        // Main layout
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.getChildren().addAll(fieldSizeBox, levelBox, checkBoxGroup, backButton);

        Scene scene = new Scene(layout, 400, 400);

        primaryStage.setTitle("Configuration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}*/


/* SALMAN IMPLEMENTATION
package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ConfigScreen extends Application {

    public Scene buildScene(Stage stage) {
        //example from JavaFXLayoutAndControl.pdf
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(10);
        grid.setVgap(10);
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");
        Label loginMsg = new Label();

        loginButton.setOnAction(e -> {
            if ("admin".equals(userField.getText()) &&
                    "password".equals(passField.getText())) {
                loginMsg.setText("Login successful!");
            } else {
                loginMsg.setText("Invalid credentials.");
            }
        });

        grid.add(userLabel, 0, 0);
        grid.add(userField, 1, 0);
        grid.add(passLabel, 0, 1);
        grid.add(passField, 1, 1);
        grid.add(loginButton, 1, 2);
        grid.add(loginMsg, 1, 3);

        CheckBox checkBox = new CheckBox("Enable Notifications");
        checkBox.setOnAction(e -> System.out.println("Notifications: " + checkBox.isSelected()));
        RadioButton rb1 = new RadioButton("Male");
        RadioButton rb2 = new RadioButton("Female");
        ToggleGroup genderGroup = new ToggleGroup();
        rb1.setToggleGroup(genderGroup);
        rb2.setToggleGroup(genderGroup);
        rb1.setOnAction(e -> System.out.println("Gender: Male"));
        rb2.setOnAction(e -> System.out.println("Gender: Female"));

        ComboBox<String> fruits = new ComboBox<>();
        fruits.getItems().addAll("Apple", "Banana", "Cherry");
        fruits.setOnAction(e -> System.out.println("Fruit: " + fruits.getValue()));

        Slider volume = new Slider(0, 100, 50);
        volume.setShowTickLabels(true);
        volume.setShowTickMarks(true);
        volume.setMajorTickUnit(25);
        volume.valueProperty().addListener((obs, oldVal, newVal) ->
                System.out.println("Volume: " + newVal.intValue()));

        VBox controlsBox = new VBox(10, new Label("Controls:"), checkBox, rb1, rb2,
                new Label("Select fruit:"), fruits, new Label("Adjust volume:"), volume);
        controlsBox.setPadding(new Insets(10));

        Button goToHomeButton = new Button("Go to Home");
        goToHomeButton.setOnAction(e -> {
            Main app = new Main();
            Scene homeScene = app.start(stage);
            stage.setScene(homeScene);
        });

        VBox bottomBox = new VBox(10, goToHomeButton);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setStyle("-fx-alignment: center;");

        BorderPane root = new BorderPane();
        root.setTop(new Label("JavaFX Layout and Control Demo"));
        root.setLeft(controlsBox);
        root.setCenter(grid);
        root.setBottom(new Label("Status: Ready"));
        root.setBottom(bottomBox);

        return new Scene(root, 500, 400);
    }
}

 */