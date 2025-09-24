package org.example.controllers;

import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import org.example.AI.AIMove;
import org.example.AI.TetrisAI;
import org.example.external.TetrisClient;
import org.example.interfaces.IGameBoard;
import org.example.model.*;

import java.io.IOException;

public class GameController extends BaseController {
    private long fallIntervalNs = 1_500_000_000L;
    private long lastFallTime = 0;
    private boolean paused = false;

    private final IGameBoard board;
    private final boolean isPlayerTwo; // decides the key mapping

    private final boolean isPlayerOneAI = SettingsStore.getInstance().get().aiPlayer1Enabled();
    private final boolean isPlayerTwoAI = SettingsStore.getInstance().get().aiPlayer2Enabled();
    private final boolean isExternalPlayer = SettingsStore.getInstance().get().extendedModeEnabled();
    private boolean gameOverDialogShown = false;
    private final int initialLevel;
    private int currentLevel;

    // P1 by default
    public GameController(IGameBoard board) {
        this(board, false, 1);
    }

    // Pass true for Player 2
    public GameController(IGameBoard board, boolean isPlayerTwo, int initialLevel) {
        this.board = board;
        this.isPlayerTwo = isPlayerTwo;
        this.initialLevel = initialLevel;
        this.currentLevel = initialLevel;
        updateFallIntervalFromSettings();
    }

    private void updateFallIntervalFromSettings() {
        GameSettings settings = SettingsStore.getInstance().get();
        int level = this.initialLevel;

        long baseInterval = 1_500_000_000L;
        long reductionPerLevel = 200_000_000L;

        long newInterval = baseInterval - (level * reductionPerLevel);
        long minInterval = 50_000_000L;
        this.fallIntervalNs = Math.max(newInterval, minInterval);
    }

    public void updateFallInterval(int newLevel) {
        this.currentLevel = newLevel;
        long baseInterval = 1_500_000_000L;
        long reductionPerLevel = 200_000_000L;

        long newInterval = baseInterval - (newLevel * reductionPerLevel);
        long minInterval = 50_000_000L;
        this.fallIntervalNs = Math.max(newInterval, minInterval);
    }

    public int getCurrentLevel() {
        return this.currentLevel;
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
        GameBoard gameBoard = (GameBoard) board;
        if (paused) return;

        if (gameBoard.isGameOver() && !gameOverDialogShown) {
            paused = true;
            gameOverDialogShown = true;
            showGameOverScreen();
            return;
        }

        if (lastFallTime == 0 || now - lastFallTime > fallIntervalNs) {

            if (gameBoard.getCurrentPiece() == null) {
                board.tick();
                lastFallTime = now;
                return;
            }

            // External Player
            if (!isPlayerTwo && isExternalPlayer) {
                try {
                    // External player makes the move when extended mode is enabled
                    PureGame pg = ((GameBoard) board).toPureGame();
                    OpMove move = new TetrisClient().requestMove(pg);

                    // Apply rotations
                    for (int i = 0; i < move.opRotate(); i++) {
                        board.rotatePiece();
                    }

                    // Move horizontally towards target opX
                    int dx = move.opX() - board.getCurrentX();
                    if (dx < 0) {
                        for (int i = 0; i < Math.abs(dx); i++) board.move(-1, 0);
                    } else if (dx > 0) {
                        for (int i = 0; i < dx; i++) board.move(1, 0);
                    }

                } catch (IOException ex) {
                    // Stop the game and show an error popup
                    paused = true;
                    javafx.application.Platform.runLater(() -> {
                        javafx.scene.control.Alert alert =
                                new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
                        alert.setTitle("External Player Error");
                        alert.setHeaderText("TetrisServer not running");
                        alert.setContentText("Extended mode requires TetrisServer to be running on port 3000.\n" +
                                "Please start TetrisServer.jar and try again.");
                        alert.showAndWait();
                    });
                    return;
                }

            }

            // Player 1 is an AI
            if (!isPlayerTwo && isPlayerOneAI && !isExternalPlayer) {
                applyLocalAIMove(gameBoard);
            }

            // Player Two is an AI
            if (isPlayerTwo && isPlayerTwoAI) {
                applyLocalAIMove(gameBoard);
            }

            if (gameBoard.getCurrentPiece() != null) {
                board.tick();
                lastFallTime = now;
                return;
            }
        }
    }

    public boolean isPaused() {
        return paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    private void applyLocalAIMove(GameBoard gameBoard) {
        PureGame pg = ((GameBoard) board).toPureGame();
        TetrisAI ai = new TetrisAI();
        AIMove bestMove = ai.findBestMove(gameBoard, gameBoard.getCurrentPiece());

        for (int i = 0; i < bestMove.getRotation(); i++) {
            board.rotatePiece();
        }

        int dx = bestMove.getColumn() - board.getCurrentX();
        for (int i = 0; i < Math.abs(dx); i++) {
            board.move(dx < 0 ? -1 : 1, 0);
        }
    }

    private void showGameOverScreen() {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Over");
            alert.setHeaderText("No more valid moves");
            alert.setContentText("The game has ended. Try again?");
            alert.showAndWait();


            GameBoard gameBoard = (GameBoard) board;
            gameBoard.resetGame();

            paused = false;
            gameOverDialogShown = false;
            lastFallTime = System.nanoTime();
        });
    }
}
