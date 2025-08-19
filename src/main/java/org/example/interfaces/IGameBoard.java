package org.example.interfaces;

import org.example.model.Tetromino;

public interface IGameBoard {
    int getGridWidth();
    int getGridHeight();
    int[][] getGrid();
    Tetromino getCurrentPiece();
    Tetromino getNextPiece();
    int getCurrentX();
    int getCurrentY();

    boolean move(int dx, int dy);
    void rotatePiece();
    void tick();
}