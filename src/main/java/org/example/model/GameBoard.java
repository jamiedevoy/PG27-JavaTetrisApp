package org.example.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.controllers.GameController;
import org.example.interfaces.IGameBoard;
import org.example.controllers.ScoreController;
import org.example.interfaces.ScoreUpdateListener;

public class GameBoard implements IGameBoard {

    // ==== defaults (kept to preserve your original behavior) ====
    public static final int DEFAULT_GRID_WIDTH  = 10;
    public static final int DEFAULT_GRID_HEIGHT = 20;

    // ==== dynamic dimensions (replace old static usage) ====
    private static int gridWidth;
    private static int gridHeight;
    private GameController gameController;
    public int iterationInt = 1;
    public int iterationScore = 0;
    public int linesErased = 0;
    public int baseScore = 100;
    private Runnable onLevelUpCallback;

    ArrayList<ScoreController> scores = new ArrayList<>();
    private String playerName;

    // grid is now sized from gridWidth x gridHeight
    private int[][] grid;

    private final Random random = new Random();

    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int currentX;
    private int currentY;
    private boolean isGameOver = false;

    // listeners
    private final List<ScoreUpdateListener> scoreUpdateListeners = new ArrayList<>();

    // ====== constructors ======

    /** Original constructor: keeps your default 10x20 board. */
    public GameBoard(String playerName) {
        this(playerName, DEFAULT_GRID_WIDTH, DEFAULT_GRID_HEIGHT);
    }

    public void setOnLevelUpCallback(Runnable callback) {
        this.onLevelUpCallback = callback;
    }

    /** NEW: explicit (cols x rows) so field size (blocks across) can vary. */
    public GameBoard(String playerName, int cols, int rows) {
        this.playerName = playerName;
        if (cols < 1 || rows < 1) {
            throw new IllegalArgumentException("cols/rows must be > 0");
        }
        this.gridWidth = cols;
        this.gridHeight = rows;
        this.grid = new int[gridHeight][gridWidth];
        spawnNewPiece(); // will also prepare nextPiece
    }

    // ====== listener plumbing ======

    public void addScoreUpdateListener(ScoreUpdateListener listener) {
        scoreUpdateListeners.add(listener);
    }

    private void notifyScoreListeners() {
        for (ScoreUpdateListener listener : scoreUpdateListeners) {
            listener.onScoreUpdated(this.scores);
        }
    }

    // ====== IGameBoard & getters ======

    @Override
    public int getGridWidth() {
        return gridWidth;
    }

    @Override
    public int getGridHeight() {
        return gridHeight;
    }

    @Override
    public int[][] getGrid() {
        return grid;
    }

    @Override
    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    @Override
    public Tetromino getNextPiece() {
        return nextPiece;
    }

    @Override
    public int getCurrentX() {
        return currentX;
    }

    @Override
    public int getCurrentY() {
        return currentY;
    }

    // ====== movement / rotation / tick ======

    private boolean move(int dx, int dy, boolean playSound) {
        if (currentPiece == null) return false;
        if (canPlace(currentX + dx, currentY + dy, currentPiece.getShape())) {
            if (playSound) {
                org.example.audio.AudioManager.getInstance().playSfx("/audio/move.wav");
            }
            currentX += dx;
            currentY += dy;
            return true;
        }
        return false;
    }

    @Override
    public boolean move(int dx, int dy) {
        return move(dx, dy, true);
    }

    public void rotatePiece() {
        if (currentPiece == null) return;
        int[][] original = currentPiece.getShape();
        org.example.audio.AudioManager.getInstance().playSfx("/audio/move.wav");
        currentPiece.rotate();
        if (!canPlace(currentX, currentY, currentPiece.getShape())) {
            // revert if invalid
            currentPiece = new Tetromino(original, currentPiece.getColorIndex());
        }
    }

    public void tick() {
        if (!move(0, 1, false)) {
            lockPiece();
            clearFullRows();
            spawnNewPiece();
        }
    }

    // ====== spawning / pieces ======

    private void spawnNewPiece() {
        currentPiece = (nextPiece == null) ? randomTetromino() : nextPiece;
        // center spawn horizontally based on current width
        currentX = Math.max(0, (gridWidth / 2) - 2); // -2 is a typical offset for 4-wide tetrominoes
        currentY = 0;
        nextPiece = randomTetromino();

        if (!canPlace(currentX, currentY, currentPiece.getShape())) {
            isGameOver = true;
        }
    }

    private Tetromino randomTetromino() {
        return TetrominoFactory.createRandom();
    }

    // ====== collision & locking ======

    private boolean canPlace(int x, int y, int[][] shape) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int gridX = x + col;
                    int gridY = y + row;
                    if (gridX < 0 || gridX >= gridWidth || gridY < 0 || gridY >= gridHeight) {
                        return false;
                    }
                    if (grid[gridY][gridX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    private void lockPiece() {
        if (currentPiece == null) return;
        int[][] shape = currentPiece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int gridX = currentX + col;
                    int gridY = currentY + row;
                    if (gridY >= 0 && gridY < gridHeight && gridX >= 0 && gridX < gridWidth) {
                        grid[gridY][gridX] = currentPiece.getColorIndex();
                    }
                }
            }
        }
    }

    // ====== row clearing / board reset ======

    private void clearFullRows() {
        int linesClearedThisTurn = 0;
        for (int y = gridHeight - 1; y >= 0; y--) {
            boolean full = true;
            for (int x = 0; x < gridWidth; x++) {
                if (grid[y][x] == 0) {
                    full = false;
                    break;
                }
            }
            if (full) {
                org.example.audio.AudioManager.getInstance().playSfx("/audio/score.wav");
                linesErased++;
                if (iterationScore == 0){
                    iterationScore = baseScore;
                } else{
                    iterationScore = linesErased * baseScore + iterationScore;
                }
                System.out.println("Iteration Score:" + iterationScore);
                linesClearedThisTurn += linesErased;
                removeRow(y);
                y++;
            }
        }

        if (linesClearedThisTurn > 0) {

            System.out.println("linesClearedThisTurn: " + iterationScore);

            if (linesErased % 3 == 0) {
                if (onLevelUpCallback != null) {
                    onLevelUpCallback.run();
                    System.out.println("triggered level-up");
                }
            }
        }
    }

    private void removeRow(int row) {
        for (int y = row; y > 0; y--) {
            System.arraycopy(grid[y - 1], 0, grid[y], 0, gridWidth);
        }
        for (int x = 0; x < gridWidth; x++) {
            grid[0][x] = 0;
        }
    }

    public void resetGame() {
        scores.add(new ScoreController(playerName, iterationInt, iterationScore, gameController.getCurrentLevel(), linesErased));
        iterationInt++;
        System.out.println("Attempt Iteration:" + iterationInt);
        iterationScore = 0;
        linesErased = 0;
        notifyScoreListeners();
        System.out.println("Attempt Count:" + iterationScore);

        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                grid[y][x] = 0;
            }
        }

        isGameOver = false;
        currentPiece = null;
        nextPiece = randomTetromino();
        spawnNewPiece();
    }

    // ====== helpers ======


    /** Optional: programmatic resize if you ever need it at runtime (not required for your current flow). */
    public void resizeGrid(int cols, int rows) {
        if (cols < 1 || rows < 1) throw new IllegalArgumentException("cols/rows must be > 0");
        this.gridWidth = cols;
        this.gridHeight = rows;
        this.grid = new int[gridHeight][gridWidth];
        this.currentX = Math.max(0, (gridWidth / 2) - 2);
        this.currentY = 0;
        this.currentPiece = null;
        this.nextPiece = randomTetromino();
    }

    public PureGame toPureGame() {
        return new PureGame(
                gridWidth,
                gridHeight,
                grid,
                currentPiece != null ? currentPiece.getShape() : new int[0][0],
                nextPiece != null ? nextPiece.getShape() : new int[0][0]
        );
    }

    public int getCurrentScore() {
        return iterationScore;
    }

    public void setGameController(GameController controller) {
        this.gameController = controller;
    }

    public ArrayList<ScoreController> getScores() {
        return scores;
    }

    public int getLevel() {
        return iterationScore;
    }

    public int getLinesCleared () {
        return linesErased;
    }
}
