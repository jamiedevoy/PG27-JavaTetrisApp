package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.example.model.GameSettings;

public class ConfigController extends BaseController {
    @FXML private Slider fieldSizeSlider;
    @FXML private Label fieldSizeValue;
    @FXML private Slider levelSlider;
    @FXML private Label levelValue;
    @FXML private CheckBox musicCheckBox;
    @FXML private CheckBox soundEffectsCheckBox;
    @FXML private CheckBox aiPlayCheckBox;
    @FXML private CheckBox extendedModeCheckBox;
    @FXML private CheckBox twoPlayerCheckBox;
    @FXML private Button backButton;
    @FXML private Button resetButton;

    private Stage primaryStage;
    private Runnable mainApp;

    public void start(Stage stage, Runnable mainApp) {
        this.primaryStage = stage;
        this.mainApp = mainApp;

        fieldSizeSlider.valueProperty().addListener((o, oldVal, newVal) ->
                fieldSizeValue.setText(Integer.toString(newVal.intValue()))
        );
        levelSlider.valueProperty().addListener((o, oldVal, newVal) ->
                levelValue.setText(Integer.toString(newVal.intValue()))
        );

        backButton.setOnAction(e -> {
            org.example.model.SettingsStore.set(getSettings());
            mainApp.run();
        });
        
        resetButton.setOnAction(e -> setToDefault());
    }

    public void setMainApp(Runnable mainApp, Stage primaryStage) {
        this.mainApp = mainApp;
        this.primaryStage = primaryStage;
    }

    // Wrap all getter methods
    public GameSettings getSettings() {
        return new GameSettings(
                getFieldSize(),
                getLevel(),
                isMusicEnabled(),
                isSoundEffectsEnabled(),
                isAIPlayEnabled(),
                isExtendedModeEnabled(),
                isTwoPlayerEnabled()
        );
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

    public boolean isTwoPlayerEnabled() {
        return twoPlayerCheckBox.isSelected();
    }

    private void setToDefault() {
        GameSettings defaults = new GameSettings(
                10,
                1,
                false,
                false,
                false,
                false,
                false);

        fieldSizeSlider.setValue(defaults.fieldSize());
        levelSlider.setValue(defaults.level());
        musicCheckBox.setSelected(defaults.musicEnabled());
        soundEffectsCheckBox.setSelected(defaults.soundEffectsEnabled());
        aiPlayCheckBox.setSelected(defaults.aiPlayEnabled());
        extendedModeCheckBox.setSelected(defaults.extendedModeEnabled());
        twoPlayerCheckBox.setSelected(defaults.twoPlayerEnabled());
    }
}