package org.example.AI;

import org.example.enums.TetrominoType;
import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TetrisAITest {

    @Test
    void findBestMoveValid() {

        GameBoard board = new GameBoard("Test", 10, 20);
        Tetromino piece = TetrominoFactory.create(TetrominoType.I);

        TetrisAI ai = new TetrisAI();
        AIMove move = ai.findBestMove(board, piece);

        assertNotNull(move, "Move returned by AI");
        assertTrue(move.getColumn() >= 0 && move.getColumn() <= board.getGridWidth() - 1,
                "column should be within the gameboard");
        assertTrue(move.getRotation() >= 0 && move.getRotation() <= 3,
                "0 and 3 is rotation range");
    }

    @Test
    void findBestMoveInvalid() {
        GameBoard board = new GameBoard("AITester", 10, 20);

        TetrisAI ai = new TetrisAI();
        AIMove move = ai.findBestMove(board, null);

        assertNotNull(move);
        assertEquals(board.getGridWidth() / 2, move.getColumn(), "column default for null piece");
        assertEquals(0, move.getRotation(), "rotation default for null piece");

    }
}