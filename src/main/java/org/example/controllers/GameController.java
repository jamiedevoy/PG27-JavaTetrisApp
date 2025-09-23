package org.example.controllers;

import javafx.scene.input.KeyCode;
import org.example.AI.AIMove;
import org.example.AI.TetrisAI;
import org.example.external.TetrisClient;
import org.example.interfaces.IGameBoard;
import org.example.model.GameBoard;
import org.example.model.OpMove;
import org.example.model.PureGame;
import org.example.model.SettingsStore;

import java.io.IOException;

public class GameController extends BaseController {
    private static final long FALL_INTERVAL_NS = 500_000_000;

    private long lastFallTime = 0;
    private boolean paused = false;

    private final IGameBoard board;
    private final boolean isPlayerTwo; // decides the key mapping

    private final boolean isPlayerOneAI = SettingsStore.getInstance().get().aiPlayer1Enabled();
    private final boolean isPlayerTwoAI = SettingsStore.getInstance().get().aiPlayer2Enabled();
    private final boolean isExternalPlayer = SettingsStore.getInstance().get().extendedModeEnabled();


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
            GameBoard gameBoard = (GameBoard) board;

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

}
