package org.example.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.view.ConfigScreen;
import org.example.view.Main;

public class GOController extends BaseController {

    @FXML public Button toMainBTN;
    @FXML public Button replayBTN;
    public StackPane overlayLayout;
    @FXML private Label scoreLabel;
    @FXML private Label levelLabel;
    @FXML private Label linesClearedLabel;

    public void initialize(GameScreenController gameScreenController) {
        toMainBTN.setOnAction(e -> gameScreenController.leaveToMain());
        replayBTN.setOnAction(e -> gameScreenController.resetGame());
    }

    public void setStats(int score, int level, int linesCleared) {
        scoreLabel.setText(Integer.toString(score));
        levelLabel.setText(Integer.toString(level));
        linesClearedLabel.setText(Integer.toString(linesCleared));
    }
}