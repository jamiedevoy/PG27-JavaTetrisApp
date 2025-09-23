package org.example.model;

public record GameSettings(
        int fieldSize,
        int level,
        boolean musicEnabled,
        boolean soundEffectsEnabled,
        boolean aiPlayer1Enabled,
        boolean aiPlayer2Enabled,
        boolean extendedModeEnabled,
        boolean twoPlayerEnabled
) {}
