package org.example;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class MainController {

    @FXML
    protected void onPlayButtonClicked() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/Game.fxml"));
            Stage mainStage = new Stage(fxmlLoader.getController());
            mainStage.initStyle(StageStyle.DECORATED);
            //mainStage.initModality(Modality.WINDOW_MODAL);
            Scene scene = new Scene(fxmlLoader.load());
            mainStage.setScene(scene);
            GameScreen.show(mainStage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
