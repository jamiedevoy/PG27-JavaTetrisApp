package org.example.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HighScoreManagerTest {
    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "highscores.json";

    @BeforeEach
    void setUp() {
        // i will test the accessibility of the file
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {

        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void saveScores() {
        List<ScoreController> scores = new ArrayList<>();
        scores.add(new ScoreController("TestingMC", 1, 1500, 10, 50));
        scores.add(new ScoreController("TestingMD", 2, 1000, 3, 20));

        HighScoreManager.saveScores(scores);

        List<ScoreController> loaded = HighScoreManager.loadScores();
        assertEquals(2, loaded.size());

        assertEquals("TestingMC", loaded.get(0).getPlayerName());
        assertEquals(1, loaded.get(0).getIterationInt());
        assertEquals(1500, loaded.get(0).getScore());
        assertEquals(10, loaded.get(0).getLevel());
        assertEquals(50, loaded.get(0).getLinesErased());

        assertEquals("TestingMD", loaded.get(1).getPlayerName());
        assertEquals(2, loaded.get(1).getIterationInt());
        assertEquals(1000, loaded.get(1).getScore());
        assertEquals(3, loaded.get(1).getLevel());
        assertEquals(20, loaded.get(1).getLinesErased());

    }

    @Test
    void clearScores() {
        List<ScoreController> scores = new ArrayList<>();
        scores.add(new ScoreController("PlayerTest", 1, 500, 2, 10));
        HighScoreManager.saveScores(scores);

        HighScoreManager.clearScores();

        List<ScoreController> loaded = HighScoreManager.loadScores();
        assertTrue(loaded.isEmpty(), "Should be empty after clearScores()");
    }

    @Test
    void loadScores() {
        File file = new File(FILE_PATH);
        if (file.exists()) {
            file.delete();
        }

        List<ScoreController> loaded = HighScoreManager.loadScores();
        assertNotNull(loaded);
        assertTrue(loaded.isEmpty(), "Return empty list if file not existing");
    }
}