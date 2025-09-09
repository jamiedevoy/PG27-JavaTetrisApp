package org.example.model;

public final class SettingsStore {
    private static GameSettings current =
        new GameSettings(10, 1, true, true, false, false, false);

    private SettingsStore() {}

    public static GameSettings get() { return current; }
    public static void set(GameSettings s) { current = s; }
}
