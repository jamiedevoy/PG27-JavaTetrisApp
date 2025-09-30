package org.example.interfaces;

import javafx.stage.Stage;

public interface IScreen {
    public void show(Stage primaryStage, Runnable mainApp);
    public void hide(Stage primaryStage, Runnable mainApp);
}
