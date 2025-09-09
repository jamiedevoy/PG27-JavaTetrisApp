package org.example.controllers;

import javafx.scene.input.KeyCode;
import org.example.interfaces.IGameBoard;

public class GameController extends BaseController {
    private static final long FALL_INTERVAL_NS = 500_000_000;

    private long lastFallTime = 0;
    private boolean paused = false;

    private final IGameBoard board;
    private final boolean isPlayerTwo; // decides the key mapping

    // P1 by default
    public GameController(IGameBoard board) {
        this(board, false);
    }

    // Pass true for Player 2
    public GameController(IGameBoard board, boolean isPlayerTwo) {
        this.board = board;
        this.isPlayerTwo = isPlayerTwo;
    }

    public void handleKey(KeyCode code) {
        if (paused) return;

        if (!isPlayerTwo) {
            // Player 1 controls: Arrows + WASD
            switch (code) {
                case LEFT, A -> board.move(-1, 0);
                case RIGHT, D -> board.move(1, 0);
                case DOWN, S -> board.move(0, 1);
                case UP, W -> board.rotatePiece();
                default -> { }
            }
        } else {
            // Player 2 controls: IJKL  (I=rotate, J=left, K=down, L=right)
            switch (code) {
                case J -> board.move(-1, 0);
                case L -> board.move(1, 0);
                case K -> board.move(0, 1);
                case I -> board.rotatePiece();
                default -> { }
            }
        }
    }

    public void update(long now) {
        if (paused) return;
        if (lastFallTime == 0 || now - lastFallTime > FALL_INTERVAL_NS) {
            board.tick();
            lastFallTime = now;
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }
}
