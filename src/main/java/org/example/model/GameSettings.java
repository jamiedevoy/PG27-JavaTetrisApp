package org.example.model;

public record GameSettings(
        int fieldSize,
        int level,
        boolean musicEnabled,
        boolean soundEffectsEnabled,
        boolean aiPlayEnabled,
        boolean extendedModeEnabled
) {}
