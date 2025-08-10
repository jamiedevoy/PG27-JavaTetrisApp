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
import org.example.model.GameBoard;
import org.example.model.Tetromino;

public class GameScreen {
    private static final int TILE_SIZE = 30;
    private static final int NEXT_PIECE_SIZE = 5;
    private static final double PAUSE_OVERLAY_ALPHA = 0.6;
    private static final int PAUSE_FONT_SIZE = 48;

    private static final Color[] COLORS = {
            Color.CYAN, Color.YELLOW, Color.PURPLE, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE
    };

    private final GameBoard board = new GameBoard();
    private final GameController controller = new GameController(board);

    private GraphicsContext gcGrid;
    private GraphicsContext gcNext;

    public static void show(Stage primaryStage, Runnable onBack) {
        new GameScreen().start(primaryStage, onBack);
    }

    private void start(Stage primaryStage, Runnable onBack) {
        Canvas gridCanvas = new Canvas(GameBoard.GRID_WIDTH * TILE_SIZE, GameBoard.GRID_HEIGHT * TILE_SIZE);
        gcGrid = gridCanvas.getGraphicsContext2D();

        Canvas nextCanvas = new Canvas(NEXT_PIECE_SIZE * TILE_SIZE, NEXT_PIECE_SIZE * TILE_SIZE);
        gcNext = nextCanvas.getGraphicsContext2D();

        StackPane nextCanvasWrapper = new StackPane(nextCanvas);
        nextCanvasWrapper.setPadding(new Insets(10));
        nextCanvasWrapper.setStyle("-fx-background-color: BLACK; -fx-background-radius: 5;");
        nextCanvasWrapper.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.7)));

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> onBack.run());

        VBox rightPane = new VBox(10);
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.getChildren().addAll(nextCanvasWrapper, backButton);

        StackPane centerPane = new StackPane(gridCanvas);
        centerPane.setPadding(new Insets(20));
        centerPane.setAlignment(Pos.CENTER);
        gridCanvas.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.7)));

        BorderPane root = new BorderPane();
        root.getStyleClass().add("border-pane-background");
        root.setCenter(centerPane); // 10/8/25 - SC - this was originally pointing to gridcanvas causing canvas to be across the entire left screen
        root.setRight(rightPane);
        //root.setStyle("-fx-background-color: #4290F580;");

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
        double width = GameBoard.GRID_WIDTH * TILE_SIZE;
        double height = GameBoard.GRID_HEIGHT * TILE_SIZE;

        // this is for the background
        gcGrid.setFill(Color.web("#4290f5", 0.3));
        gcGrid.fillRect(0, 0, width, height);

        // then we draw our blocks
        int[][] grid = board.getGrid();
        for (int y = 0; y < GameBoard.GRID_HEIGHT; y++) {
            for (int x = 0; x < GameBoard.GRID_WIDTH; x++) {
                if (grid[y][x] != 0) {
                    gcGrid.setFill(COLORS[grid[y][x] - 1]);
                    gcGrid.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    //gcGrid.setStroke(Color.WHITE);
                    gcGrid.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        drawPiece(board.getCurrentPiece(), board.getCurrentX(), board.getCurrentY());
        drawNextPiece();

        if (controller.isPaused()) {
            gcGrid.setFill(new Color(0, 0, 0, PAUSE_OVERLAY_ALPHA));
            gcGrid.fillRect(0, 0, GameBoard.GRID_WIDTH * TILE_SIZE, GameBoard.GRID_HEIGHT * TILE_SIZE);
            gcGrid.setFill(Color.WHITE);
            gcGrid.setFont(Font.font(PAUSE_FONT_SIZE));
            gcGrid.fillText("PAUSED", (GameBoard.GRID_WIDTH * TILE_SIZE) / 2 - 80, (GameBoard.GRID_HEIGHT * TILE_SIZE) / 2);
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
                        //gcGrid.setStroke(Color.BLACK);
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
                        //gcNext.setStroke(Color.BLACK);
                        gcNext.strokeRect((col + offsetX) * TILE_SIZE, (row + offsetY) * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }
    }
}