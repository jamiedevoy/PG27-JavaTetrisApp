package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.controllers.ScoreController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HighScoreManager {
    private static final String FILE_PATH = System.getProperty("user.home") + File.separator + "highscores.json";

    public static void saveScores(List<ScoreController> scores) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            mapper.writeValue(new File(FILE_PATH), scores);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving high scores.");
        }
    }

    public static void clearScores() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);

        try {
            // i will try and write an empty array which should hopefully clear it
            mapper.writeValue(file, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error clearing high scores.");
        }
    }

    public static List<ScoreController> loadScores() {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return mapper.readValue(file, mapper.getTypeFactory().constructCollectionType(List.class, ScoreController.class));
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading high scores.");
            return new ArrayList<>();
        }
    }
}