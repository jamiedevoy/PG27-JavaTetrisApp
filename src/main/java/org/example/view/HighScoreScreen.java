package org.example.view;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.example.controllers.HighScoreManager;
import org.example.controllers.ScoreController;

import java.util.*;

public class HighScoreScreen {
    public static void show(Stage primaryStage, Runnable onBack) {

        TableView<ScoreController> table = new TableView<>();
        table.setEditable(true);
        table.getStyleClass().add("high-score-table");


        TableColumn<ScoreController, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("playerName"));
        nameCol.setCellFactory(TextFieldTableCell.forTableColumn());
        nameCol.setOnEditCommit(event -> {
            ScoreController score = event.getRowValue();
            score.setPlayerName(event.getNewValue());
        });

        StringConverter<Integer> integerConverter = new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object != null ? object.toString() : "";
            }

            @Override
            public Integer fromString(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    return 0;
                }
            }
        };

        // Table column for Iteration
        TableColumn<ScoreController, Integer> iterationCol = new TableColumn<>("Attempt");
        iterationCol.setCellValueFactory(new PropertyValueFactory<>("iterationInt"));
        iterationCol.setCellFactory(TextFieldTableCell.forTableColumn(integerConverter));
        iterationCol.setOnEditCommit(event -> {
            ScoreController score = event.getRowValue();
            score.setIterationInt(event.getNewValue());
        });

        // Table column for Score
        TableColumn<ScoreController, Integer> scoreCol = new TableColumn<>("Score");
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));
        scoreCol.setCellFactory(TextFieldTableCell.forTableColumn(integerConverter));
        scoreCol.setOnEditCommit(event -> {
            ScoreController score = event.getRowValue();
            score.setScore(event.getNewValue());
        });

        TableColumn<ScoreController, Integer> levelCol = new TableColumn<>("Level");
        levelCol.setCellValueFactory(new PropertyValueFactory<>("level"));
        levelCol.setCellFactory(TextFieldTableCell.forTableColumn(integerConverter));
        levelCol.setOnEditCommit(event -> {
            ScoreController score = event.getRowValue();
            score.setLevel(event.getNewValue());
        });

        TableColumn<ScoreController, Integer> linesCol = new TableColumn<>("Lines");
        linesCol.setCellValueFactory(new PropertyValueFactory<>("linesErased"));
        linesCol.setCellFactory(TextFieldTableCell.forTableColumn(integerConverter));
        linesCol.setOnEditCommit(event -> {
            ScoreController score = event.getRowValue();
            score.setLinesErased(event.getNewValue());
        });

        table.getColumns().addAll(nameCol, iterationCol, scoreCol, levelCol, linesCol);

        // Load scores from the JSON file
        List<ScoreController> scores = HighScoreManager.loadScores();
        table.setItems(FXCollections.observableArrayList(scores));

        List<ScoreController> topScores = scores.stream()
                .limit(10)
                .collect(java.util.stream.Collectors.toList());

        table.setItems(FXCollections.observableArrayList(topScores));

        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> onBack.run());

        Button saveButton = new Button("Save");
        saveButton.setOnAction(e -> {
            // Get the list of all scores from the table view
            List<ScoreController> allScores = new ArrayList<>(table.getItems());
            // Save the entire list to the JSON file
            HighScoreManager.saveScores(allScores);
            System.out.println("High scores saved!");
        });

        // NEW: Button to clear scores
        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            HighScoreManager.clearScores();
            table.setItems(FXCollections.observableArrayList());
        });

        Button addButton = new Button("Add");
        addButton.setOnAction(e -> {

            Dialog<ScoreController> saveDialog = new Dialog<>();
            saveDialog.setTitle("Add New High Score");
            saveDialog.setHeaderText("Enter score details manually.");


            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            saveDialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            TextField nameField = new TextField();
            nameField.setPromptText("Player Name");
            // Use TextFields instead of Spinners
            TextField scoreField = new TextField();
            scoreField.setPromptText("Score");
            TextField levelField = new TextField();
            levelField.setPromptText("Level");
            TextField linesField = new TextField();
            linesField.setPromptText("Lines Erased");

            grid.add(new Label("Name:"), 0, 0);
            grid.add(nameField, 1, 0);
            grid.add(new Label("Score:"), 0, 1);
            grid.add(scoreField, 1, 1);
            grid.add(new Label("Level:"), 0, 2);
            grid.add(levelField, 1, 2);
            grid.add(new Label("Lines Erased:"), 0, 3);
            grid.add(linesField, 1, 3);

            saveDialog.getDialogPane().setContent(grid);

            saveDialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    try {
                        return new ScoreController(
                                nameField.getText(),
                                1,
                                Integer.parseInt(scoreField.getText()),
                                Integer.parseInt(levelField.getText()),
                                Integer.parseInt(linesField.getText())
                        );
                    } catch (NumberFormatException ex) {
                        System.err.println("Please enter only digits.");
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Invalid Input");
                        errorAlert.showAndWait();
                        return null;
                    }
                }
                return null;
            });

            Optional<ScoreController> result = saveDialog.showAndWait();
            result.ifPresent(newScore -> {

                List<ScoreController> allScores = HighScoreManager.loadScores();
                allScores.add(newScore);


                HighScoreManager.saveScores(allScores);

                Collections.sort(allScores, Comparator.comparingInt(ScoreController::getScore).reversed());
                List<ScoreController> updatedTopScores = allScores.stream()
                        .limit(10)
                        .collect(java.util.stream.Collectors.toList());
                table.setItems(FXCollections.observableArrayList(updatedTopScores));

                System.out.println("Scores Updated!");
            });
        });

        HBox bottomBox = new HBox(backButton, saveButton, clearButton, addButton);
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
