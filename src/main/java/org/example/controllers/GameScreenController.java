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
import org.example.model.GameSettings;
import org.example.model.SettingsStore;
import org.example.model.Tetromino;

import java.util.ArrayList;
import java.util.List;

public class GameScreenController extends BaseController implements ScoreUpdateListener<ScoreController> {

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
    @FXML private Label currentLevelP1;
    @FXML private Label p1NameLabel;
    @FXML private Label p1ScoreLabel;

    // P2 canvases & UI
    @FXML private Canvas gridCanvasP2;
    @FXML private Canvas nextCanvasP2;
    @FXML private Label currentScoreP2;
    @FXML private ListView<ScoreController> scoreViewP2;
    @FXML private Label currentLevelP2;
    @FXML private Label p2NameLabel;
    @FXML private Label p2ScoreLabel;


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

    // Tile size is computed to fit the fixed canvas for the chosen cols/rows
    private int tileSizeP1 = 30;
    private int tileSizeP2 = 30;

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
        start(stage, mainApp, playerName, null, false);
    }

    public void start(Stage stage, Runnable mainApp, String p1Name, String p2Name, boolean twoPlayer) {
        this.primaryStage = stage;
        this.mainApp = mainApp;
        this.twoPlayerMode = twoPlayer;
        GameSettings settings = SettingsStore.getInstance().get();
        int initialLevel = settings.level();
        int cols = Math.max(6, settings.fieldSize());
        int rows = Math.max(12, cols * 2);

        gameBoard1 = new GameBoard(p1Name, cols, rows);
        gameController1 = new GameController(gameBoard1, false, initialLevel);
        gameBoard1.setGameController(gameController1);
        gameBoard1.setOnLevelUpCallback(() -> {
            int newLevel = gameController1.getCurrentLevel() + 1;
            gameController1.updateFallInterval(newLevel);
            System.out.println("New Level");
        });

        if (twoPlayerMode) {
            gameBoard2 = new GameBoard(p2Name, cols, rows);
            gameController2 = new GameController(gameBoard2, true, initialLevel);
            gameBoard2.setGameController(gameController2);
        }

        tileSizeP1 = computeTileSizeToFit(gridCanvasP1, cols, rows);
        if (twoPlayerMode) {
            tileSizeP2 = computeTileSizeToFit(gridCanvasP2, cols, rows);
        }

        p1NameLabel.setText(p1Name);
        if (twoPlayerMode) {
            p2NameLabel.setText(p2Name);
        }

        p1ScoreLabel.setText(p1Name + " Attempts");
        if (twoPlayerMode) {
            p2ScoreLabel.setText(p2Name + " Attempts");
        }

        // GCs
        gcP1 = gridCanvasP1.getGraphicsContext2D();
        gcNextP1 = nextCanvasP1.getGraphicsContext2D();
        if (twoPlayerMode) {
            gcP2 = gridCanvasP2.getGraphicsContext2D();
            // THIS IS THE CORRECTED LINE
            gcNextP2 = nextCanvasP2.getGraphicsContext2D();
        }

        // Show/hide P2 column and P2 scores based on mode
        p2Col.setManaged(twoPlayerMode);
        p2Col.setVisible(twoPlayerMode);
        p2ScoresWrapper.setManaged(twoPlayerMode);
        p2ScoresWrapper.setVisible(twoPlayerMode);

        // Attempts lists
        observableScoresP1 = FXCollections.observableArrayList(gameBoard1.getScores());
        scoreViewP1.setItems(observableScoresP1);
        gameBoard1.addScoreUpdateListener(this::onScoreUpdatedP1);

        if (twoPlayerMode) {
            observableScoresP2 = FXCollections.observableArrayList(gameBoard2.getScores());
            scoreViewP2.setItems(observableScoresP2);
            gameBoard2.addScoreUpdateListener(this::onScoreUpdatedP2);
        }

        backButton.setOnAction(e -> {
            List<ScoreController> allScores =
                    java.util.stream.Stream.<java.util.List<ScoreController>>of(
                                    gameBoard1.getScores(),
                                    (twoPlayerMode && gameBoard2 != null) ? gameBoard2.getScores() : java.util.List.of(),
                                    HighScoreManager.loadScores()
                            )
                            .flatMap(java.util.List::stream)
                            .sorted(java.util.Comparator.comparingInt(ScoreController::getScore).reversed())
                            .toList();

            HighScoreManager.saveScores(allScores);
            SettingsStore.getInstance().resetBoardSize();
            try { org.example.audio.AudioManager.getInstance().stopMusic(); } catch (Throwable ignored) {}

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

    private int computeTileSizeToFit(Canvas canvas, int cols, int rows) {
        double tileW = canvas.getWidth() / Math.max(1, cols);
        double tileH = canvas.getHeight() / Math.max(1, rows);
        int t = (int)Math.floor(Math.min(tileW, tileH));
        return Math.max(8, t);
    }

    private void draw() {
        // P1
        drawBoardAndPiece(gameBoard1, gcP1, tileSizeP1);
        drawNextPiece(gcNextP1, gameBoard1, tileSizeP1);
        currentScoreP1.setText("Score: " + gameBoard1.getCurrentScore());
        currentLevelP1.setText("Level: " + gameController1.getCurrentLevel());

        // P2
        if (twoPlayerMode) {
            drawBoardAndPiece(gameBoard2, gcP2, tileSizeP2);
            drawNextPiece(gcNextP2, gameBoard2, tileSizeP2);
            currentScoreP2.setText("Score: " + gameBoard2.getCurrentScore());
            currentLevelP2.setText("Level: " + gameController2.getCurrentLevel());

        }

        if (globallyPaused) {
            pauseOverlay(gcP1);
            if (twoPlayerMode) pauseOverlay(gcP2);
        }
    }

    private void drawBoardAndPiece(GameBoard board, GraphicsContext gc, int tileSize) {
        // clear background
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());

        // locked blocks
        int[][] grid = board.getGrid();
        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] != 0) {
                    gc.setFill(COLORS[grid[y][x] - 1]);
                    gc.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
                    gc.setStroke(Color.BLACK);
                    gc.strokeRect(x * tileSize, y * tileSize, tileSize, tileSize);
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
                        int drawX = (board.getCurrentX() + col) * tileSize;
                        int drawY = (board.getCurrentY() + row) * tileSize;
                        gc.fillRect(drawX, drawY, tileSize, tileSize);
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(drawX, drawY, tileSize, tileSize);
                    }
                }
            }
        }
    }

    private void drawNextPiece(GraphicsContext gc, GameBoard board, int tileSize) {
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
                        int drawX = (col + offsetX) * tileSize;
                        int drawY = (row + offsetY) * tileSize;
                        gc.fillRect(drawX, drawY, tileSize, tileSize);
                        gc.setStroke(Color.BLACK);
                        gc.strokeRect(drawX, drawY, tileSize, tileSize);
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

    // ScoreUpdateListener for P1
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