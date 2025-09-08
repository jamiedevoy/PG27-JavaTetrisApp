package org.example.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.controllers.ScoreController;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;



public class HighScoreManager {
    private static final String FILE_PATH = "C:\\Users\\HP\\Documents\\Uni\\2006ICT\\java-projects\\PG27-JavaTetrisApp\\highscores.json";

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