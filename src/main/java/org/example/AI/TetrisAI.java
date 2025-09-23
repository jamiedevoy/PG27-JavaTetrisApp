package org.example.AI;

import org.example.model.GameBoard;
import org.example.model.Tetromino;
import org.example.model.TetrominoFactory;

public class TetrisAI {

    private final BoardEvaluator evaluator = new BoardEvaluator();

    public AIMove findBestMove(GameBoard gameBoard, Tetromino piece) {
        if (piece == null || piece.getShape() == null) {
            return new AIMove(GameBoard.GRID_WIDTH / 2, 0);
        }

        AIMove bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        int rotationLimit = getRotationLimit();

        for (int rotation = 0; rotation < rotationLimit; rotation++) {
            Tetromino rotatedPiece = TetrominoFactory.copyOf(piece);
            for (int i = 0; i < rotation; i++) {
                rotatedPiece.rotate();
            }

            int pieceWidth = rotatedPiece.getShape()[0].length;
            int maxCol = GameBoard.GRID_WIDTH - pieceWidth;

            for (int col = 0; col <= maxCol; col++) {
                int[][] simulatedBoard = simulateDrop(gameBoard.getGrid(), rotatedPiece, col);
                int score = evaluator.evaluateBoard(simulatedBoard);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new AIMove(col, rotation);
                }
            }
        }

        return bestMove;
    }

    private int[][] simulateDrop(int[][] board, Tetromino piece, int col) {
        int[][] simulatedBoard = copyBoard(board);
        int row = 0;
        while (canPlacePiece(simulatedBoard, piece, col, row)) {
            row++;
        }
        row--; // last valid row
        if (!canPlacePiece(simulatedBoard, piece, col, row)) {
            return null; // Invalid Placement
        }

        placePiece(simulatedBoard, piece, col, row);
        return simulatedBoard;
    }

    private boolean canPlacePiece(int[][] board, Tetromino piece, int col, int row) {
        int[][] shape = piece.getShape();
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    int boardX = col + x;
                    int boardY = row + y;
                    if (boardX < 0 || boardX >= board[0].length || boardY < 0 || boardY >= board.length) {
                        return false;
                    }
                    if (board[boardY][boardX] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placePiece(int[][] board, Tetromino piece, int col, int row) {
        int[][] shape = piece.getShape();
        for (int y = 0; y < shape.length; y++) {
            for (int x = 0; x < shape[y].length; x++) {
                if (shape[y][x] != 0) {
                    int boardX = col + x;
                    int boardY = row + y;
                    if (boardX >= 0 && boardX < board[0].length && boardY >= 0 && boardY < board.length) {
                        board[boardY][boardX] = 1;
                    }
                }
            }
        }
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][board[0].length];
        for (int y = 0; y < board.length; y++) {
            System.arraycopy(board[y], 0, newBoard[y], 0, board[0].length);
        }
        return newBoard;
    }

    private int getRotationLimit() {
        // use tetromino type for the limit
        return 4;
    }
}
