package org.example.model;

import org.example.enums.TetrominoType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TetrominoTest {

    @Test
    void fromType() {
        Tetromino t = Tetromino.fromType(TetrominoType.I);
        assertNotNull(t.getShape());
        assertEquals(TetrominoType.I.colorIndex, t.getColorIndex());
    }

    @Test
    void getShape() {
        Tetromino t = Tetromino.fromType(TetrominoType.T);
        int[][] shape = t.getShape();

        assertNotNull(shape);

        int[][] expected = TetrominoType.T.shape;
        for (int r = 0; r < expected.length; r++) {
            assertArrayEquals(expected[r], shape[r], "Row " + r + "matching");
        }
    }

    @Test
    void getColorIndex() {
        Tetromino t = Tetromino.fromType(TetrominoType.S);
        int color = t.getColorIndex();

        assertEquals(TetrominoType.S.colorIndex, color);
    }

    @Test
    void rotate() {
        int[][] shape = {{1, 2}, {3, 4}};
        Tetromino t = new Tetromino(shape, 5);
        t.rotate();

        int[][] expected = {{3, 1}, {4, 2}};
        assertArrayEquals(expected, t.getShape());
    }
}