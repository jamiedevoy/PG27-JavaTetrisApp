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
    @FXML private CheckBox aiP1CheckBox;
    @FXML private CheckBox aiP2CheckBox;
    @FXML private CheckBox extendedModeCheckBox;
    @FXML private CheckBox twoPlayerCheckBox;
    @FXML private Button backButton;
    @FXML private Button resetButton;

    private Stage primaryStage;
    private Runnable mainApp;

    public void start(Stage stage, Runnable mainApp) {
        this.primaryStage = stage;
        this.mainApp = mainApp;

        // Set initial UI values from saved settings
        GameSettings current = org.example.model.SettingsStore.getInstance().get();

        fieldSizeSlider.setValue(current.fieldSize());
        levelSlider.setValue(current.level());
        musicCheckBox.setSelected(current.musicEnabled());
        soundEffectsCheckBox.setSelected(current.soundEffectsEnabled());
        aiP1CheckBox.setSelected(current.aiPlayer1Enabled());
        aiP2CheckBox.setSelected(current.aiPlayer2Enabled());
        extendedModeCheckBox.setSelected(current.extendedModeEnabled());
        twoPlayerCheckBox.setSelected(current.twoPlayerEnabled());

        // Keep labels in sync with sliders
        fieldSizeValue.setText(Integer.toString((int) fieldSizeSlider.getValue()));
        levelValue.setText(Integer.toString((int) levelSlider.getValue()));

        fieldSizeSlider.valueProperty().addListener((o, oldVal, newVal) ->
                fieldSizeValue.setText(Integer.toString(newVal.intValue()))
        );
        levelSlider.valueProperty().addListener((o, oldVal, newVal) ->
                levelValue.setText(Integer.toString(newVal.intValue()))
        );

        backButton.setOnAction(e -> {
            org.example.model.SettingsStore.getInstance().set(getSettings());
            mainApp.run();
        });

        resetButton.setOnAction(e -> setToDefault());
    }

    // Wrap all getter methods
    public GameSettings getSettings() {
        return new GameSettings(
                getFieldSize(),
                getLevel(),
                isMusicEnabled(),
                isSoundEffectsEnabled(),
                isAIPlayer1Enabled(),
                isAIPlayer2Enabled(),
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

    public boolean isAIPlayer1Enabled() {
        return aiP1CheckBox.isSelected();
    }

    public boolean isAIPlayer2Enabled() {
        return aiP2CheckBox.isSelected();
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
                false,
                false);

        fieldSizeSlider.setValue(defaults.fieldSize());
        levelSlider.setValue(defaults.level());
        musicCheckBox.setSelected(defaults.musicEnabled());
        soundEffectsCheckBox.setSelected(defaults.soundEffectsEnabled());
        aiP1CheckBox.setSelected(defaults.aiPlayer1Enabled());
        aiP2CheckBox.setSelected(defaults.aiPlayer2Enabled());
        extendedModeCheckBox.setSelected(defaults.extendedModeEnabled());
        twoPlayerCheckBox.setSelected(defaults.twoPlayerEnabled());
    }
}