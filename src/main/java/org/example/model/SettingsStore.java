package org.example.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public final class SettingsStore {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final SettingsStore INSTANCE = new SettingsStore();
    private static final String SETTINGS_FILE = "settings.json";

    private GameSettings current;

    private SettingsStore() {
        this.current = load();
    }

    public static SettingsStore getInstance() {
        return INSTANCE;
    }

    public GameSettings get() {
        return current;
    }

    public void set(GameSettings s) {
        this.current = s;
        save(s);
    }

    private GameSettings load() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try {
                return mapper.readValue(file, GameSettings.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // fallback defaults
        return new GameSettings(10, 1, false, false, false, false, false, false);
    }

    private void save(GameSettings settings) {
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(SETTINGS_FILE), settings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}