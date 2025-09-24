package org.example.controllers;

public class ScoreController {
    private String playerName;
    private int iterationInt;
    private int score;
    private int level;
    private int linesErased;


    // I had to include this as the pom file dependency was causing issues without it
    public ScoreController() {
    }

    public ScoreController(String playerName, int iterationInt, int score, int level, int linesErased) {
        this.playerName = playerName;
        this.iterationInt = iterationInt;
        this.score = score;
        this.level = level;
        this.linesErased = linesErased;
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

    public int getLevel() {
        return level;
    }

    public int getLinesErased() {
        return linesErased;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setIterationInt(int iterationInt) {
        this.iterationInt = iterationInt;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setLinesErased(int linesErased) {
        this.linesErased = linesErased;
    }
}