package org.example.interfaces;

import org.example.controllers.ScoreController;

import java.util.ArrayList;

public interface ScoreUpdateListener<T> {
    void onScoreUpdated(ArrayList<T> newScores);
}

