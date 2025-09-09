package org.example.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.example.enums.TetrominoType;
import org.example.interfaces.IGameBoard;
import org.example.controllers.ScoreController;
import org.example.interfaces.ScoreUpdateListener;

public class GameBoard implements IGameBoard {


    public static final int GRID_WIDTH = 10;
    public static final int GRID_HEIGHT = 20;
    private static final int START_X = 3;
    public int iterationInt = 1;
    public int iterationScore = 0;
    ArrayList<ScoreController> scores = new ArrayList<>();
    private String playerName;
    private final int[][] grid = new int[GRID_HEIGHT][GRID_WIDTH];
    private final Random random = new Random();

    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int currentX;
    private int currentY;


    // The list of listeners is still here
    private List<ScoreUpdateListener> scoreUpdateListeners = new ArrayList<>();

    // Method to add a listener
    public void addScoreUpdateListener(ScoreUpdateListener listener) {
        scoreUpdateListeners.add(listener);
    }

    // Method to notify all listeners
    private void notifyScoreListeners() {
        for (ScoreUpdateListener listener : scoreUpdateListeners) {
            listener.onScoreUpdated(this.scores);
        }
    }
    public GameBoard(String playerName) {
        this.playerName = playerName;
        spawnNewPiece();
        //nextPiece = randomTetromino();
    }

    @Override
    public int getGridWidth() {
        return GRID_WIDTH;
    }

    @Override
    public int getGridHeight() {
        return GRID_HEIGHT;
    }

    public int[][] getGrid() {
        return grid;
    }

    public Tetromino getCurrentPiece() {
        return currentPiece;
    }

    public Tetromino getNextPiece() {
        return nextPiece;
    }

    public int getCurrentX() {
        return currentX;
    }

    public int getCurrentY() {
        return currentY;
    }

    private boolean move(int dx, int dy, boolean playSound) {
        if (canPlace(currentX + dx, currentY + dy, currentPiece.getShape())) {
            if (playSound) {
                org.example.audio.AudioManager.playSfx("/audio/move.wav");
            }
            currentX += dx;
            currentY += dy;
            return true;
        }
        return false;
    }

    // Public method for user moves (default → play sound)
    @Override
    public boolean move(int dx, int dy) {
        return move(dx, dy, true);
    }

    public void rotatePiece() {
        int[][] original = currentPiece.getShape();
        org.example.audio.AudioManager.playSfx("/audio/move.wav");
        currentPiece.rotate();
        if (!canPlace(currentX, currentY, currentPiece.getShape())) {
            // Revert if invalid
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

    private void spawnNewPiece() {
        currentPiece = nextPiece == null ? randomTetromino() : nextPiece;
        currentX = START_X;
        currentY = 0;
        nextPiece = randomTetromino();

        if (!canPlace(currentX, currentY, currentPiece.getShape())) {
            clearBoard();

        }
    }

    private Tetromino randomTetromino() {
        TetrominoType type = TetrominoType.values()[random.nextInt(TetrominoType.values().length)];
        return Tetromino.fromType(type);
    }

    private boolean canPlace(int x, int y, int[][] shape) {
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int gridX = x + col;
                    int gridY = y + row;
                    if (gridX < 0 || gridX >= GRID_WIDTH || gridY < 0 || gridY >= GRID_HEIGHT) {
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

    private void lockPiece() {
        int[][] shape = currentPiece.getShape();
        for (int row = 0; row < shape.length; row++) {
            for (int col = 0; col < shape[row].length; col++) {
                if (shape[row][col] != 0) {
                    int gridX = currentX + col;
                    int gridY = currentY + row;
                    if (gridY >= 0 && gridY < GRID_HEIGHT && gridX >= 0 && gridX < GRID_WIDTH) {
                        grid[gridY][gridX] = currentPiece.getColorIndex();
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
                //org.example.audio.AudioManager.playSfx("/audio/sscore.wav");
                iterationScore++;
                System.out.println("Iteration Score:" + iterationScore);
                removeRow(y);
                y++;
            }
        }
    }

    private void removeRow(int row) {
        for (int y = row; y > 0; y--) {
            System.arraycopy(grid[y - 1], 0, grid[y], 0, GRID_WIDTH);
        }
        for (int x = 0; x < GRID_WIDTH; x++) {
            grid[0][x] = 0;
        }
    }

    private void clearBoard() {
        scores.add(new ScoreController(playerName, iterationInt, iterationScore));
        iterationInt++;
        System.out.println("Attempt Iteration:" + iterationInt);
        // We reset our score based on clearing board
        iterationScore = 0;
        notifyScoreListeners();
        System.out.println("Attempt Count:" + iterationScore);

        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                grid[y][x] = 0;

            }
        }
    }
    public int getCurrentScore() {
        return iterationScore;
    }

    public ArrayList<ScoreController> getScores() {
        return scores;
    }
}
