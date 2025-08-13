package org.example.controller;

import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import org.example.interfaces.IGameBoard;

public class GameController {
    private static final long FALL_INTERVAL_NS = 500_000_000;
    private long lastFallTime = 0;
    private boolean paused = false;
    private final IGameBoard board;

    public GameController(IGameBoard board) {
        this.board = board;
    }

    public void handleKey(KeyCode code) {
        if (code == KeyCode.P) {
            paused = !paused;
            return;
        }
        if (paused) return;

        switch (code) {
            case LEFT -> board.move(-1, 0);
            case RIGHT -> board.move(1, 0);
            case DOWN -> board.move(0, 1);
            case UP -> board.rotatePiece();
        }
    }

    public void start(AnimationTimer timer) {
        timer.start();
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
}