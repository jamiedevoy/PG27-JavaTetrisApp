package org.example.AI;

import org.example.model.GameBoard;
import org.example.model.OpMove;
import org.example.model.Tetromino;
import org.example.model.TetrominoFactory;

public class TetrisAI {

    private final BoardEvaluator evaluator = new BoardEvaluator();

    public OpMove findBestMove(GameBoard gameBoard, Tetromino piece) {
        OpMove bestMove = null; //Utilise existing move Record
        int bestScore = Integer.MIN_VALUE;

        int[][] originalShape = piece.getShape();
        int rotationLimit = getRotationLimit(piece);

        for (int rotation = 0; rotation < rotationLimit; rotation++) {
            Tetromino rotatedPiece = TetrominoFactory.copyOf(piece);
            for (int i = 0; i < rotation; i++) {
                rotatedPiece.rotate();
            }

            int pieceWidth = rotatedPiece.getShape()[0].length;
            for (int col = 0; col <= GameBoard.GRID_WIDTH - pieceWidth; col++) {
                int[][] simulatedBoard = simulateDrop(gameBoard.getGrid(), rotatedPiece, col);
                int score = evaluator.evaluateBoard(simulatedBoard);

                if (score > bestScore) {
                    bestScore = score;
                    bestMove = new OpMove(col, rotation);
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
        placePiece(simulatedBoard, piece, col, row);
        return simulatedBoard;
    }

    private boolean canPlacePiece(int[][] board, Tetromino piece, int col, int row) {
        for (int y = 0; y < piece.getShape()[0].length; y++) {
            for (int x = 0; x < piece.getShape()[y].length; x++) {
                if (piece.getShape()[y][x] != 0) {
                    int boardX = col + x;
                    int boardY = row + y;

                    if (boardX < 0 || boardX >= board[0].length || boardY < 0 || boardY >= board[0].length) {
                        return false;
                    }
                    if (board[boardX][boardY] != 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private void placePiece(int[][] board, Tetromino piece, int col, int row) {
        for (int y = 0; y < piece.getShape().length; y++) {
            for (int x = 0; x < piece.getShape()[y].length; x++) {
                if (piece.getShape()[y][x] != 0) {
                    board[row + y][col + x] = 1; // maybe colour index?
                }
            }
        }
    }

    private int[][] copyBoard(int[][] board) {
        int[][] newBoard = new int[board.length][board[0].length];
        for (int y = 0; y < board.length; y++) {
            System.arraycopy(board[y], 0, newBoard[y], 0, board.length);
        }
        return newBoard;
    }

    private int getRotationLimit(Tetromino piece) {
        // use tetromino type for the limit
        return 4;
    }
}
