package org.example.AI;

public class BoardEvaluator {

     public int evaluateBoard(int[][] board) {
         int heightScore = getHeight(board);
         int holesScore = getHoles(board);
         int linesCleared = getClearedLines(board);
         int bumpinessScore = getBumpiness(board);
         System.out.println("Height: " + heightScore +
                 ", holes: " + holesScore +
                 ", Lines: " + linesCleared +
                 ", bumpiness: " + bumpinessScore);

         // -4,  3,  5,  2 -- X
         // -3,  3,  5,  2 -- x
         // -3,  3,  5,  2 -- x
         // -6,  3,  5,  2 -- x
         return (-6 * heightScore) + (4 * linesCleared) + (-6 * bumpinessScore) - (1 * holesScore);
     }

    private int getHeight(int[][] board) {
         int height = 0;
         for (int x = 0; x < board[0].length; x++) {
             for (int y = 0; y < board.length; y++) {
                 if (board[y][x] != 0) {
                     height = Math.max(height, board.length - y);
                     break;
                 }
             }
         }
         return height;
    }

    private int getHoles(int[][] board) {
        int holes = 0;
        for (int x = 0; x < board[0].length; x++) {
            boolean foundBlock = false;
            for (int[] ints : board) {
                if (ints[x] != 0) {
                    foundBlock = true;
                } else if (foundBlock) {
                    holes++;
                }
            }
        }
        return holes;
    }

    private int getClearedLines(int[][] board) {
        int clearedLines = 0;
        for (int[] ints : board) {
            boolean isLineFull = true;
            for (int x = 0; x < board[0].length; x++) {
                if (ints[x] == 0) {
                    isLineFull = false;
                    break;
                }
            }
            if (isLineFull) {
                clearedLines++;
            }
        }
        return clearedLines;
    }

    private int getBumpiness(int[][] board) {
         int bumpiness = 0;
         for (int x = 0; x < board[0].length - 1; x++) {
             int colHeight1 = getColumnHeight(board, x);
             int colHeight2 = getColumnHeight(board, x + 1);
             bumpiness += Math.abs(colHeight1 - colHeight2);
         }
         return bumpiness;
    }

    private int getColumnHeight(int[][] board, int col) {
         if (col < 0 || col >= board[0].length) {
             return 0;
         }
         for (int y = 0; y < board.length; y++) {
             if (board[y][col] != 0) {
                 return board.length - y;
             }
         }
         return 0;
    }
}
