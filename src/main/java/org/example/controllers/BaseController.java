package org.example.controllers;

import javafx.stage.Stage;

public abstract class BaseController {
    protected Stage primaryStage;

    public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
}