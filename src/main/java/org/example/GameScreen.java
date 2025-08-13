package org.example;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import org.example.controller.GameController;
import org.example.interfaces.IGameBoard;
import org.example.model.Tetromino;

public class GameScreen {
    private static final int TILE_SIZE = 30;
    private static final int NEXT_PIECE_SIZE = 5;
    private static final double PAUSE_OVERLAY_ALPHA = 0.6;
    private static final int PAUSE_FONT_SIZE = 48;

    private static final Color[] COLORS = {
            Color.CYAN, Color.YELLOW, Color.PURPLE, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE
    };

    private final IGameBoard board;
    private final GameController controller;

    private GraphicsContext gcGrid;
    private GraphicsContext gcNext;

    public GameScreen(IGameBoard board) {
        this.board = board;
        this.controller = new GameController(board);
    }

    public void show(Stage primaryStage, Runnable onBack) {
        start(primaryStage, onBack);
    }

    private void start(Stage primaryStage, Runnable onBack) {
        // --- Game grid canvas ---
        Canvas gridCanvas = new Canvas(board.getGridWidth() * TILE_SIZE, board.getGridHeight() * TILE_SIZE);
        gcGrid = gridCanvas.getGraphicsContext2D();
        gridCanvas.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.7)));

        StackPane centerPane = new StackPane(gridCanvas);
        centerPane.setAlignment(Pos.CENTER);
        centerPane.setPadding(Insets.EMPTY); // Override the root padding so we can move pieces fully R, L
        centerPane.getStyleClass().add("grid-container");

        // --- Next piece canvas ---
        Canvas nextCanvas = new Canvas(NEXT_PIECE_SIZE * TILE_SIZE, NEXT_PIECE_SIZE * TILE_SIZE);
        gcNext = nextCanvas.getGraphicsContext2D();

        StackPane nextCanvasWrapper = new StackPane(nextCanvas);
        nextCanvasWrapper.setPadding(Insets.EMPTY);
        nextCanvasWrapper.setStyle("-fx-background-color: transparent;");
        nextCanvasWrapper.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.7)));

        // --- Back button ---
        Button backButton = new Button("Back to Menu");
        backButton.getStyleClass().add("arcade-button");
        backButton.setOnAction(e -> onBack.run());

        // --- Right pane with next canvas on top and button at bottom ---
        VBox rightPane = new VBox(10);
        rightPane.setAlignment(Pos.TOP_CENTER);

        // Spacer to push button all the way down
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        rightPane.getChildren().addAll(nextCanvasWrapper, spacer, backButton);
        rightPane.setPadding(new Insets(10, 0, 0, 0));

        // --- Root pane ---
        BorderPane root = new BorderPane();
        root.getStyleClass().add("border-pane-background");
        root.setPadding(new Insets(15, 20, 15, 20));
        root.setCenter(centerPane);
        root.setRight(rightPane);

        // --- Scene ---
        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        scene.setOnKeyPressed(e -> {
            controller.handleKey(e.getCode());
            draw();
        });

        primaryStage.setTitle("Tetris - Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.setFocusTraversable(true);
        root.requestFocus();
        scene.setOnMouseClicked(e -> root.requestFocus());

        // --- Animation timer ---
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                controller.update(now);
                draw();
            }
        };
        controller.start(timer);
    }

    private void draw() {
        double width = board.getGridWidth()  * TILE_SIZE;
        double height = board.getGridHeight()  * TILE_SIZE;

        gcGrid.setFill(Color.BLACK);
        gcGrid.fillRect(0, 0, width, height);

        int[][] grid = board.getGrid();
        for (int y = 0; y < board.getGridHeight(); y++) {
            for (int x = 0; x < board.getGridWidth(); x++) {
                if (grid[y][x] != 0) {
                    gcGrid.setFill(COLORS[grid[y][x] - 1]);
                    gcGrid.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gcGrid.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        drawPiece(board.getCurrentPiece(), board.getCurrentX(), board.getCurrentY());
        drawNextPiece();

        if (controller.isPaused()) {
            gcGrid.setFill(new Color(0, 0, 0, PAUSE_OVERLAY_ALPHA));
            gcGrid.fillRect(0, 0, board.getGridWidth() * TILE_SIZE, board.getGridHeight() * TILE_SIZE);
            gcGrid.setFill(Color.WHITE);
            gcGrid.setFont(Font.font(PAUSE_FONT_SIZE));
            gcGrid.fillText("PAUSED", (board.getGridWidth() * TILE_SIZE) / 2 - 80, (board.getGridHeight() * TILE_SIZE) / 2);
        }
    }

    private void drawPiece(Tetromino piece, int x, int y) {
        if (piece != null) {
            gcGrid.setFill(COLORS[piece.getColorIndex() - 1]);
            int[][] shape = piece.getShape();
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] != 0) {
                        gcGrid.fillRect((x + col) * TILE_SIZE, (y + row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        gcGrid.strokeRect((x + col) * TILE_SIZE, (y + row) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
    }

    private void drawNextPiece() {
        gcNext.setFill(Color.BLACK);
        gcNext.fillRect(0, 0, NEXT_PIECE_SIZE * TILE_SIZE, NEXT_PIECE_SIZE * TILE_SIZE);

        Tetromino nextPiece = board.getNextPiece();
        if (nextPiece != null) {
            gcNext.setFill(COLORS[nextPiece.getColorIndex() - 1]);
            int offsetX = 1, offsetY = 1;
            int[][] shape = nextPiece.getShape();
            for (int row = 0; row < shape.length; row++) {
                for (int col = 0; col < shape[row].length; col++) {
                    if (shape[row][col] != 0) {
                        gcNext.fillRect((col + offsetX) * TILE_SIZE, (row + offsetY) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                        gcNext.strokeRect((col + offsetX) * TILE_SIZE, (row + offsetY) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
    }
}