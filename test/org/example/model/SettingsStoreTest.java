package org.example.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class SettingsStoreTest {

    private static final String SETTINGS_FILE = "settings.json";


    @BeforeEach
    void setUp() {
        // i will test the accessibility of the file
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @AfterEach
    void tearDown() {

        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    void getInstance() {
        SettingsStore store = SettingsStore.getInstance();
        assertNotNull(store);
    }

    @Test
    void getwithset() {
        SettingsStore store = SettingsStore.getInstance();
        GameSettings gs = new GameSettings(15, 2, true, true, false, false, false, false);

        store.set(gs);
        GameSettings loaded = store.get();

        assertEquals(15, loaded.fieldSize());
        assertEquals(2, loaded.level());
        assertFalse(loaded.twoPlayerEnabled());
    }

    @Test
    void save() {
        SettingsStore store = SettingsStore.getInstance();
        GameSettings gs = new GameSettings(12, 3, true, false, true, false, false, false);
        store.set(gs);

        // Force new instance (simulating app restart)
        SettingsStore fresh = SettingsStore.getInstance();
        GameSettings reloaded = fresh.get();

        assertEquals(12, reloaded.fieldSize());
        assertEquals(3, reloaded.level());
    }

    @Test
    void resetBoardSize() {
        SettingsStore store = SettingsStore.getInstance();
        store.resetBoardSize();
        assertNotNull(store.get());
    }
}