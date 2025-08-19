package org.example.model;
import java.util.Random;

import org.example.enums.TetrominoType;
import org.example.interfaces.IGameBoard;

public class GameBoard implements IGameBoard {
    public static final int GRID_WIDTH = 10;
    public static final int GRID_HEIGHT = 20;
    private static final int START_X = 3;

    private final int[][] grid = new int[GRID_HEIGHT][GRID_WIDTH];
    private final Random random = new Random();

    private Tetromino currentPiece;
    private Tetromino nextPiece;
    private int currentX;
    private int currentY;

    public GameBoard() {
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

    public boolean move(int dx, int dy) {
        //System.out.println(canPlace(currentX + dx, currentY + dy, currentPiece.getShape()));// debug
        if (canPlace(currentX + dx, currentY + dy, currentPiece.getShape())) {
            currentX += dx;
            currentY += dy;
            return true;
        }
        return false;
    }

    public void rotatePiece() {
        int[][] original = currentPiece.getShape();
        currentPiece.rotate();
        if (!canPlace(currentX, currentY, currentPiece.getShape())) {
            // Revert if invalid
            currentPiece = new Tetromino(original, currentPiece.getColorIndex());
        }
    }

    public void tick() {
        if (!move(0, 1)) {
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
            clearBoard(); // Game over placeholder
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
        for (int y = 0; y < GRID_HEIGHT; y++) {
            for (int x = 0; x < GRID_WIDTH; x++) {
                grid[y][x] = 0;
            }
        }
    }
}
