package org.example.controllers;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import org.example.model.GameBoard;
import org.example.model.Tetromino;

public class GameScreenController {

    @FXML private BorderPane gameLayout;
    @FXML private StackPane gridWrapper;
    @FXML private Canvas gridCanvas;
    @FXML private Canvas nextCanvas;
    @FXML private Button backButton;

    private GraphicsContext gcGrid;
    private GraphicsContext gcNext;
    private Stage primaryStage;
    private Runnable mainApp;

    private GameBoard gameBoard;
    private GameController gameController;

    private static final int TILE_SIZE = 30;
    private static final Color[] COLORS = {
            Color.CYAN, Color.YELLOW, Color.PURPLE, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE
    };

    public void initialize() {}

    public void start(Stage stage, Runnable mainApp) {
        this.primaryStage = stage;
        this.mainApp = mainApp;

        gcGrid = gridCanvas.getGraphicsContext2D();
        gcNext = nextCanvas.getGraphicsContext2D();

        gameBoard = new GameBoard();
        gameController = new GameController(gameBoard);

        backButton.setOnAction(e -> mainApp.run());

        Platform.runLater(() -> gridCanvas.requestFocus());

        gridCanvas.setOnKeyPressed(e -> gameController.handleKey(e.getCode()));

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                gameController.update(now);
                draw();
            }
        };
        timer.start();
    }

    private void draw() {
        int[][] grid = gameBoard.getGrid();
        gcGrid.setFill(Color.BLACK);
        gcGrid.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());

        for (int y = 0; y < grid.length; y++) {
            for (int x = 0; x < grid[y].length; x++) {
                if (grid[y][x] != 0) {
                    gcGrid.setFill(COLORS[grid[y][x] - 1]);
                    gcGrid.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gcGrid.setStroke(Color.BLACK);
                    gcGrid.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        Tetromino current = gameBoard.getCurrentPiece();
        if (current != null) {
            gcGrid.setFill(COLORS[current.getColorIndex() - 1]);
            for (int row = 0; row < current.getShape().length; row++) {
                for (int col = 0; col < current.getShape()[row].length; col++) {
                    if (current.getShape()[row][col] != 0) {
                        int drawX = (gameBoard.getCurrentX() + col) * TILE_SIZE;
                        int drawY = (gameBoard.getCurrentY() + row) * TILE_SIZE;
                        gcGrid.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                        gcGrid.setStroke(Color.BLACK);
                        gcGrid.strokeRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        drawNextPiece();

        if (gameController.isPaused()) {
            gcGrid.setFill(new Color(0, 0, 0, 0.6));
            gcGrid.fillRect(0, 0, gridCanvas.getWidth(), gridCanvas.getHeight());
            gcGrid.setFill(Color.WHITE);
            gcGrid.setFont(Font.font(48));
            gcGrid.fillText("PAUSED", gridCanvas.getWidth() / 2 - 80, gridCanvas.getHeight() / 2);
        }
    }

    private void drawNextPiece() {
        gcNext.setFill(Color.BLACK);
        gcNext.fillRect(0, 0, nextCanvas.getWidth(), nextCanvas.getHeight());

        Tetromino next = gameBoard.getNextPiece();
        if (next != null) {
            gcNext.setFill(COLORS[next.getColorIndex() - 1]);
            int offsetX = 1;
            int offsetY = 1;
            for (int row = 0; row < next.getShape().length; row++) {
                for (int col = 0; col < next.getShape()[row].length; col++) {
                    if (next.getShape()[row][col] != 0) {
                        int drawX = (col + offsetX) * TILE_SIZE;
                        int drawY = (row + offsetY) * TILE_SIZE;
                        gcNext.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                        gcNext.setStroke(Color.BLACK);
                        gcNext.strokeRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
    }
}


