package org.example.model;

import org.example.enums.TetrominoType;

import java.util.Random;

public final class TetrominoFactory {
    private static final Random random = new Random();

    private TetrominoFactory() {}

    // Create a Tetromino from a given type
    public static Tetromino create(TetrominoType type) {
        return Tetromino.fromType(type);
    }

    // Create a random Tetromino
    public static Tetromino createRandom() {
        TetrominoType type = TetrominoType.values()[random.nextInt(TetrominoType.values().length)];
        return Tetromino.fromType(type);
    }
}
