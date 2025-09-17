package org.example.model;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public final class SettingsStore {
    private static final String SETTINGS_FILE = "settings.json";
    private static final ObjectMapper mapper = new ObjectMapper();

    private static GameSettings current;

    private SettingsStore() {}

    public static GameSettings get() {
        if (current == null) {
            current = load();
        }
        return current;
    }

    public static void set(GameSettings s) {
        current = s;
        save(s);
    }

    private static GameSettings load() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try {
                return mapper.readValue(file, GameSettings.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // defaults if no file exists or load fails
        return new GameSettings(10, 1, false, false, false, false, false);
    }

    private static void save(GameSettings settings) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(SETTINGS_FILE), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}