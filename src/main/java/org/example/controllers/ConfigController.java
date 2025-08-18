package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ConfigController {
    @FXML private Slider fieldSizeSlider;
    @FXML private Label fieldSizeValue;
    @FXML private Slider levelSlider;
    @FXML private Label levelValue;
    @FXML private CheckBox musicCheckBox;
    @FXML private CheckBox soundEffectsCheckBox;
    @FXML private CheckBox aiPlayCheckBox;
    @FXML private CheckBox extendedModeCheckBox;
    @FXML private Button backButton;
    @FXML private Button resetButton;

    private Stage primaryStage;
    private Runnable mainApp;

    public void start(Stage stage, Runnable mainApp) {
        this.primaryStage = stage;
        this.mainApp = mainApp;

        // show feild size value on slider change
        fieldSizeSlider.valueProperty().addListener((o, oldVal, newVal) -> {
            fieldSizeValue.setText(Integer.toString(newVal.intValue()));
            // Have the thing do thing for value change
        });
        levelSlider.valueProperty().addListener((o, oldVal, newVal) -> {
            levelValue.setText(Integer.toString(newVal.intValue()));
            // more doing things
        });

        // back button shenanigans
        backButton.setOnAction(e -> {mainApp.run();});

        // Reset button, incorp records for this?
        resetButton.setOnAction(e ->{setToDefault();});
    }

    public void setMainApp(Runnable mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
    }

    public int getFieldSize() {
        return (int) fieldSizeSlider.getValue();
    }

    public int getLevel() {
        return (int) levelSlider.getValue();
    }

    public boolean isMusicEnabled() {
        return musicCheckBox.isSelected();
    }

    public boolean isSoundEffectsEnabled() {
        return soundEffectsCheckBox.isSelected();
    }

    public boolean isAIPlayEnabled() {
        return aiPlayCheckBox.isSelected();
    }

    public boolean isExtendedModeEnabled() {
        return extendedModeCheckBox.isSelected();
    }

    private void setToDefault() {
        System.out.println("Setting default values, coming soon to a programme near you!");
    }
}
