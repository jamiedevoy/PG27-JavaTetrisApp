package org.example.controllers;

public class ScoreController {
    private String playerName;
    private int iterationInt;
    private int score;


    // I had to include this as the pom file dependency was causing issues without it
    public ScoreController() {
    }

    public ScoreController(String playerName, int iterationInt, int score) {
        this.playerName = playerName;
        this.iterationInt = iterationInt;
        this.score = score;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getIterationInt() {
        return iterationInt;
    }

    public int getScore() {
        return score;
    }
}