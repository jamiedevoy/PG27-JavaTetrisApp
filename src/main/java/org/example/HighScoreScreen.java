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


public class HighScoreScreen {
    public static void show(Stage primaryStage, Runnable onBack) {
        TableView<ScoreEntry> table = new TableView<>();

        TableColumn<ScoreEntry, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<ScoreEntry, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        TableColumn<ScoreEntry, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));

        table.getColumns().addAll(nameCol, scoreCol, levelCol);
        table.setItems(getDummyScores());
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

    private static ObservableList<ScoreEntry> getDummyScores() {
        return FXCollections.observableArrayList(
                new ScoreEntry("Jamie", 1500, 3),
                new ScoreEntry("Alex", 1200, 2),
                new ScoreEntry("Sam", 1000, 1),
                new ScoreEntry("Taylor", 950, 1),
                new ScoreEntry("Jordan", 850, 1)
        );
    }

    public static class ScoreEntry {
        private final String name;
        private final int score;
        private final int level;

        public ScoreEntry(String name, int score, int level) {
            this.name = name;
            this.score = score;
            this.level = level;
        }

        public String getName() { return name; }
        public int getScore() { return score; }
        public int getLevel() { return level; }
    }
}
