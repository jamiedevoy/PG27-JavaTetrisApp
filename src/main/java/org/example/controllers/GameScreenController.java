package org.example.controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.interfaces.ScoreUpdateListener;
import org.example.model.GameBoard;
import org.example.model.Tetromino;

import java.util.ArrayList;
import java.util.List;

public class GameScreenController extends BaseController implements ScoreUpdateListener {

    @FXML private BorderPane gameLayout;

    // Layout nodes to toggle for 1P vs 2P
    @FXML private HBox playersRow;
    @FXML private VBox p1Col;
    @FXML private VBox p2Col;
    @FXML private VBox p2ScoresWrapper;

    // P1 canvases & UI
    @FXML private Canvas gridCanvasP1;
    @FXML private Canvas nextCanvasP1;
    @FXML private Label currentScoreP1;
    @FXML private ListView<ScoreController> scoreViewP1;

    // P2 canvases & UI
    @FXML private Canvas gridCanvasP2;
    @FXML private Canvas nextCanvasP2;
    @FXML private Label currentScoreP2;
    @FXML private ListView<ScoreController> scoreViewP2;

    @FXML private Button backButton;

    // GCs
    private GraphicsContext gcP1;
    private GraphicsContext gcNextP1;
    private GraphicsContext gcP2;
    private GraphicsContext gcNextP2;

    private Stage primaryStage;
    private Runnable mainApp;

    // Boards & controllers
    private GameBoard gameBoard1;
    private GameController gameController1;

    private boolean twoPlayerMode = false;
    private GameBoard gameBoard2;
    private GameController gameController2;

    // attempt lists
    private ObservableList<ScoreController> observableScoresP1;
    private ObservableList<ScoreController> observableScoresP2;

    private static final int TILE_SIZE = 30;
    private static final Color[] COLORS = {
            Color.CYAN, Color.YELLOW, Color.PURPLE, Color.GREEN,
            Color.RED, Color.BLUE, Color.ORANGE
    };

    private boolean globallyPaused = false;

    @FXML
    public void initialize() {
        // list cell renderers
        scoreViewP1.setCellFactory(param -> new ListCell<ScoreController>() {
            @Override
            protected void updateItem(ScoreController item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Attempt " + item.getIterationInt() + ": " + item.getScore());
            }
        });
        scoreViewP2.setCellFactory(param -> new ListCell<ScoreController>() {
            @Override
            protected void updateItem(ScoreController item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : "Attempt " + item.getIterationInt() + ": " + item.getScore());
            }
        });
    }

    // keep legacy signature
    public void start(Stage stage, Runnable mainApp, String playerName) {
        start(stage, mainApp, playerName, false);
    }

    public void start(Stage stage, Runnable mainApp, String playerName, boolean twoPlayer) {
        this.primaryStage = stage;
        this.mainApp = mainApp;
        this.twoPlayerMode = twoPlayer;

        // GCs
        gcP1 = gridCanvasP1.getGraphicsContext2D();
        gcNextP1 = nextCanvasP1.getGraphicsContext2D();

        if (twoPlayerMode) {
            gcP2 = gridCanvasP2.getGraphicsContext2D();
            gcNextP2 = nextCanvasP2.getGraphicsContext2D();
        }

        // Boards & controllers
        gameBoard1 = new GameBoard(twoPlayerMode ? "Player 1" : playerName);
        gameController1 = new GameController(gameBoard1, false);

        if (twoPlayerMode) {
            gameBoard2 = new GameBoard("Player 2");
            gameController2 = new GameController(gameBoard2, true);
        }

        // Show/hide P2 column and P2 scores based on mode
        p2Col.setManaged(twoPlayerMode);
        p2Col.setVisible(twoPlayerMode);
        p2ScoresWrapper.setManaged(twoPlayerMode);
        p2ScoresWrapper.setVisible(twoPlayerMode);

        // Attempts lists
        observableScoresP1 = FXCollections.observableArrayList(gameBoard1.getScores());
        scoreViewP1.setItems(observableScoresP1);
        gameBoard1.addScoreUpdateListener(newScores -> onScoreUpdatedP1(newScores));

        if (twoPlayerMode) {
            observableScoresP2 = FXCollections.observableArrayList(gameBoard2.getScores());
            scoreViewP2.setItems(observableScoresP2);
            gameBoard2.addScoreUpdateListener(newScores -> onScoreUpdatedP2(newScores));
        }

        // Back: merge & save scores, return to menu
        backButton.setOnAction(e -> {
            List<ScoreController> newScores = new ArrayList<>(gameBoard1.getScores());
            if (twoPlayerMode && gameBoard2 != null) {
                newScores.addAll(gameBoard2.getScores());
            }
            List<ScoreController> allScores = HighScoreManager.loadScores();
            allScores.addAll(newScores);
            allScores.sort((s1, s2) -> Integer.compare(s2.getScore(), s1.getScore()));
            HighScoreManager.saveScores(allScores);

            // Stop music when exiting game
            org.example.audio.AudioManager.stopMusic();

            mainApp.run();
        });

        // Focus & keys
        Platform.runLater(() -> gridCanvasP1.requestFocus());
        gameLayout.setOnMouseClicked(event -> gridCanvasP1.requestFocus());

        gridCanvasP1.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case P -> {
                    globallyPaused = !globallyPaused;
                    gameController1.setPaused(globallyPaused);
                    if (twoPlayerMode && gameController2 != null) {
                        gameController2.setPaused(globallyPaused);
                    }
                }
                default -> {
                    gameController1.handleKey(e.getCode());
                    if (twoPlayerMode) gameController2.handleKey(e.getCode());
                }
            }
        });

        // Main loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!globallyPaused) {
                    gameController1.update(now);
                    if (twoPlayerMode && gameController2 != null) {
                        gameController2.update(now);
                    }
                }
                draw();
            }
        };
        timer.start();
    }

    private void draw() {
        // P1
        drawBoardAndPiece(gameBoard1, gcP1);
        drawNextPiece(gcNextP1, gameBoard1);
        currentScoreP1.setText("Score: " + gameBoard1.getCurrentScore());

        // P2
        if (twoPlayerMode) {
            drawBoardAndPiece(gameBoard2, gcP2);
            drawNextPiece(gcNextP2, gameBoard2);
            currentScoreP2.setText("Score: " + gameBoard2.getCurrentScore());
        }

        if (globallyPaused) {
            // Dim both canvases
            pauseOverlay(gcP1);
            if (twoPlayerMode) pauseOverlay(gcP2);
        }
    }

    private void drawBoardAndPiece(GameBoard board, GraphicsContext gc) {
        // clear background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // locked blocks
        int[][] grid = board.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] != 0) {
                    gc.setFill(COLORS[grid[y][x] - 1]);
                    gc.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // falling piece
        Tetromino current = board.getCurrentPiece();
        if (current != null) {
            gc.setFill(COLORS[current.getColorIndex() - 1]);
            for (int row = 0; row < current.getShape().length; row++) {
                for (int col = 0; col < current.getShape()[row].length; col++) {
                    if (current.getShape()[row][col] != 0) {
                        int drawX = (board.getCurrentX() + col) * TILE_SIZE;
                        int drawY = (board.getCurrentY() + row) * TILE_SIZE;
                        gc.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
    }

    private void drawNextPiece(GraphicsContext gc, GameBoard board) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        Tetromino next = board.getNextPiece();
        if (next != null) {
            gc.setFill(COLORS[next.getColorIndex() - 1]);
            int offsetX = 1;
            int offsetY = 1;
            for (int row = 0; row < next.getShape().length; row++) {
                for (int col = 0; col < next.getShape()[row].length; col++) {
                    if (next.getShape()[row][col] != 0) {
                        int drawX = (col + offsetX) * TILE_SIZE;
                        int drawY = (row + offsetY) * TILE_SIZE;
                        gc.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
    }

    private void pauseOverlay(GraphicsContext gc) {
        gc.setFill(new Color(0, 0, 0, 0.6));
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font(36));
        gc.fillText("PAUSED", gc.getCanvas().getWidth() / 2 - 60, gc.getCanvas().getHeight() / 2);
    }

    // ScoreUpdateListener for P1 (original interface updates one list at a time)
    @Override
    public void onScoreUpdated(ArrayList<ScoreController> newScores) {
        onScoreUpdatedP1(newScores);
    }

    private void onScoreUpdatedP1(ArrayList<ScoreController> newScores) {
        if (observableScoresP1 == null) return;
        Platform.runLater(() -> observableScoresP1.setAll(newScores));
    }

    // Helper for P2
    private void onScoreUpdatedP2(ArrayList<ScoreController> newScores) {
        if (observableScoresP2 == null) return;
        Platform.runLater(() -> observableScoresP2.setAll(newScores));
    }
}
