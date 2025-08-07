package org.example.model;

import org.example.enums.TetrominoType;

public class Tetromino {
    private int[][] shape;
    private final int colorIndex;

    public Tetromino(int[][] shape, int colorIndex) {
        this.shape = copyShape(shape);
        this.colorIndex = colorIndex;
    }

    public static Tetromino fromType(TetrominoType type) {
        return new Tetromino(type.shape, type.colorIndex);
    }

    public int[][] getShape() {
        return shape;
    }

    public int getColorIndex() {
        return colorIndex;
    }

    public void rotate() {
        int rows = shape.length;
        int cols = shape[0].length;
        int[][] rotated = new int[cols][rows];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                rotated[c][rows - 1 - r] = shape[r][c];
            }
        }
        shape = rotated;
    }

    private int[][] copyShape(int[][] original) {
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            copy[i] = original[i].clone();
        }
        return copy;
    }
}
