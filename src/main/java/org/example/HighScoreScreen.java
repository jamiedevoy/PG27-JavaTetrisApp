package org.example;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.example.controllers.HighScoreManager;
import org.example.controllers.ScoreController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.util.List;

public class HighScoreScreen {
    public static void show(Stage primaryStage, Runnable onBack) {

        // We will be using the ScoreController class
        TableView<ScoreController> table = new TableView<>();
        table.getStyleClass().add("high-score-table");

        // Table column for Player Name
        TableColumn<ScoreController, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));

        // Table column for Iteration
        TableColumn<ScoreController, Integer> iterationCol = new TableColumn<>("Attempt");
        iterationCol.setCellValueFactory(new PropertyValueFactory<>("iterationInt"));

        // Table column for Score
        TableColumn<ScoreController, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        table.getColumns().addAll(nameCol, iterationCol, scoreCol);

        // Load scores from the JSON file
        List<ScoreController> scores = HighScoreManager.loadScores();
        table.setItems(FXCollections.observableArrayList(scores));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> onBack.run());

        HBox bottomBox = new HBox(backButton);
        bottomBox.setPadding(new Insets(10));
        bottomBox.setSpacing(10);

        BorderPane layout = new BorderPane();
        layout.getStyleClass().add("border-pane-background");
        layout.setPadding(new Insets(15));
        layout.setCenter(table);
        layout.setBottom(bottomBox);

        Scene scene = new Scene(layout, 400, 300);
        scene.getStylesheets().add(HighScoreScreen.class.getResource("/styles.css").toExternalForm());
        primaryStage.setScene(scene);
    }
}
