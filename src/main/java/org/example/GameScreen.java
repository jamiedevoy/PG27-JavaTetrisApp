package org.example;

import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Random;

public class GameScreen {

    private static final int TILE_SIZE = 30;
    private static final int GRID_WIDTH = 10;
    private static final int GRID_HEIGHT = 20;
    private static final int NEXT_PIECE_SIZE = 5;

    private static final Color[] COLORS = {
            Color.CYAN, Color.YELLOW, Color.PURPLE, Color.GREEN, Color.RED, Color.BLUE, Color.ORANGE
    };

    // The grid stores fixed blocks (placed pieces)
    private int[][] grid = new int[GRID_HEIGHT][GRID_WIDTH];

    // Current falling piece info
    private Tetromino currentPiece;
    private int currentX = 3;  // Starting X (middle-ish)
    private int currentY = 0;  // Start at top row

    private boolean paused = false;

    private Tetromino nextPiece;

    private Random random = new Random();

    private long lastFallTime = 0;
    private static final long FALL_INTERVAL_NS = 500_000_000; // 0.5 seconds

    private GraphicsContext gcGrid;
    private GraphicsContext gcNext;

    public static void show(Stage primaryStage, Runnable onBack) {
        GameScreen gameScreen = new GameScreen();
        gameScreen.start(primaryStage, onBack);
    }

    private void start(Stage primaryStage, Runnable onBack) {
        Canvas gridCanvas = new Canvas(GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);
        gcGrid = gridCanvas.getGraphicsContext2D();

        Canvas nextCanvas = new Canvas(NEXT_PIECE_SIZE * TILE_SIZE, NEXT_PIECE_SIZE * TILE_SIZE);
        gcNext = nextCanvas.getGraphicsContext2D();

        Button backButton = new Button("Back to Menu");
        backButton.setOnAction(e -> onBack.run());

        VBox rightPane = new VBox(10);
        rightPane.setAlignment(Pos.TOP_CENTER);
        rightPane.getChildren().addAll(nextCanvas, backButton);

        BorderPane root = new BorderPane();
        root.setCenter(gridCanvas);
        root.setRight(rightPane);
        root.setStyle("-fx-background-color: black;");

        Scene scene = new Scene(root);

        primaryStage.setTitle("Tetris - Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize pieces
        currentPiece = randomTetromino();
        nextPiece = randomTetromino();

        draw();

        // Key controls
        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.P) {
                paused = !paused;
                draw();
            }

            if (paused) return;

            if (e.getCode() == KeyCode.LEFT) {
                moveIfValid(currentX - 1, currentY, currentPiece.shape);
            } else if (e.getCode() == KeyCode.RIGHT) {
                moveIfValid(currentX + 1, currentY, currentPiece.shape);
            } else if (e.getCode() == KeyCode.DOWN) {
                moveIfValid(currentX, currentY + 1, currentPiece.shape);
            } else if (e.getCode() == KeyCode.UP) {
                int[][] rotated = rotateMatrix(currentPiece.shape);
                if (canPlace(currentX, currentY, rotated)) {
                    currentPiece.shape = rotated;
                }
            }
            draw();
        });
        root.requestFocus();  // Make sure keyboard events are captured

        primaryStage.setTitle("Tetris - Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Animation timer to drop piece every 0.5 seconds
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (paused) return;
                if (lastFallTime == 0 || now - lastFallTime > FALL_INTERVAL_NS) {
                    if (!moveIfValid(currentX, currentY + 1, currentPiece.shape)) {
                        // Can't move down -> lock piece and spawn new
                        lockPiece();
                        clearFullRows();
                        spawnNextPiece();
                    }
                    draw();
                    lastFallTime = now;
                }
            }
        };
        timer.start();
    }

    private boolean moveIfValid(int newX, int newY, int[][] shape) {
        if (canPlace(newX, newY, shape)) {
            currentX = newX;
            currentY = newY;
            return true;
        }
        return false;
    }

    private boolean canPlace(int x, int y, int[][] shape) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int gridX = x + col;
                    int gridY = y + row;
                    if (gridX < 0 || gridX >= GRID_WIDTH || gridY < 0 || gridY >= GRID_HEIGHT) {
                        return false; // Out of bounds
                    }
                    if (grid[gridY][gridX] != 0) {
                        return false; // Space taken
                    }
                }
            }
        }
        return true;
    }

    private void lockPiece() {
        for (int row = 0; row < currentPiece.shape.length; row++) {
            for (int col = 0; col < currentPiece.shape[row].length; col++) {
                if (currentPiece.shape[row][col] != 0) {
                    int gridX = currentX + col;
                    int gridY = currentY + row;
                    if (gridY >= 0 && gridY < GRID_HEIGHT && gridX >= 0 && gridX < GRID_WIDTH) {
                        grid[gridY][gridX] = currentPiece.colorIndex;
                    }
                }
            }
        }
    }

    private void clearFullRows() {
        for (int y = GRID_HEIGHT - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                removeRow(y);
                y++; // re-check same row after removal
            }
        }
    }

    private void removeRow(int row) {
        for (int y = row; y > 0; y--) {
            System.arraycopy(grid[y-1], 0, grid[y], 0, GRID_WIDTH);
        }
        // Clear top row
        for (int x = 0; x < GRID_WIDTH; x++) {
            grid[0][x] = 0;
        }
    }

    private void spawnNextPiece() {
        currentPiece = nextPiece;
        currentX = 3;
        currentY = 0;
        nextPiece = randomTetromino();

        if (!canPlace(currentX, currentY, currentPiece.shape)) {
            // Game Over - for now just clear grid
            for (int y = 0; y < GRID_HEIGHT; y++) {
                for (int x = 0; x < GRID_WIDTH; x++) {
                    grid[y][x] = 0;
                }
            }
        }
    }

    private Tetromino randomTetromino() {
        int r = random.nextInt(7);
        switch (r) {
            case 0: return new Tetromino(new int[][]{
                    {1, 1, 1, 1} // I shape
            }, 1);
            case 1: return new Tetromino(new int[][]{
                    {2, 2},
                    {2, 2} // O shape
            }, 2);
            case 2: return new Tetromino(new int[][]{
                    {0, 3, 0},
                    {3, 3, 3} // T shape
            }, 3);
            case 3: return new Tetromino(new int[][]{
                    {0, 4, 4},
                    {4, 4, 0} // S shape
            }, 4);
            case 4: return new Tetromino(new int[][]{
                    {5, 5, 0},
                    {0, 5, 5} // Z shape
            }, 5);
            case 5: return new Tetromino(new int[][]{
                    {6, 0, 0},
                    {6, 6, 6} // J shape
            }, 6);
            default: return new Tetromino(new int[][]{
                    {0, 0, 7},
                    {7, 7, 7} // L shape
            }, 7);
        }
    }

    private int[][] rotateMatrix(int[][] matrix) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        int[][] rotated = new int[cols][rows];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rotated[c][rows - 1 - r] = matrix[r][c];
            }
        }
        return rotated;
    }

    private void draw() {
        // Clear background
        gcGrid.setFill(Color.BLACK);
        gcGrid.fillRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);

        // Draw locked blocks
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                if (grid[y][x] != 0) {
                    gcGrid.setFill(COLORS[grid[y][x] - 1]);
                    gcGrid.fillRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                    gcGrid.setStroke(Color.BLACK);
                    gcGrid.strokeRect(x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Draw current piece
        if (currentPiece != null) {
            gcGrid.setFill(COLORS[currentPiece.colorIndex - 1]);
            for (int row = 0; row < currentPiece.shape.length; row++) {
                for (int col = 0; col < currentPiece.shape[row].length; col++) {
                    if (currentPiece.shape[row][col] != 0) {
                        int drawX = (currentX + col) * TILE_SIZE;
                        int drawY = (currentY + row) * TILE_SIZE;
                        gcGrid.fillRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                        gcGrid.setStroke(Color.BLACK);
                        gcGrid.strokeRect(drawX, drawY, TILE_SIZE, TILE_SIZE);
                    }
                }
            }
        }

        drawNextPiece();

        if (paused) {
            gcGrid.setFill(new Color(0, 0, 0, 0.6));
            gcGrid.fillRect(0, 0, GRID_WIDTH * TILE_SIZE, GRID_HEIGHT * TILE_SIZE);

            gcGrid.setFill(Color.WHITE);
            gcGrid.setFont(javafx.scene.text.Font.font(48));
            gcGrid.fillText("PAUSED", (GRID_WIDTH * TILE_SIZE) / 2 - 80, (GRID_HEIGHT * TILE_SIZE) / 2);
        }
    }

    private void drawNextPiece() {
        gcNext.setFill(Color.BLACK);
        gcNext.fillRect(0, 0, NEXT_PIECE_SIZE * TILE_SIZE, NEXT_PIECE_SIZE * TILE_SIZE);

        if (nextPiece != null) {
            gcNext.setFill(COLORS[nextPiece.colorIndex - 1]);
            int offsetX = 1; // center the piece in 5x5 area
            int offsetY = 1;
            for (int row = 0; row < nextPiece.shape.length; row++) {
                for (int col = 0; col < nextPiece.shape[row].length; col++) {
                    if (nextPiece.shape[row][col] != 0) {
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

    private static class Tetromino {
        int[][] shape;
        int colorIndex;

        Tetromino(int[][] shape, int colorIndex) {
            this.shape = shape;
            this.colorIndex = colorIndex;
        }
    }
}
