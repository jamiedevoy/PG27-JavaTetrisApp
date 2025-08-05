package org.example;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

public class ExitScreen {
    public static void confirmExit(Stage stage, Runnable onCancel) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want to exit?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Exit Confirmation");
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                stage.close();
            } else {
                onCancel.run();
            }
        });
    }
}
