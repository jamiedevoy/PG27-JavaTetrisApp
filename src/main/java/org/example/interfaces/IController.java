package org.example.interfaces;

import javafx.stage.Stage;
import org.example.Main;

public interface IController {
    void initialize();
    void start();

    public default void setMainApp(Main mainApp, Stage primaryStage) {}
}