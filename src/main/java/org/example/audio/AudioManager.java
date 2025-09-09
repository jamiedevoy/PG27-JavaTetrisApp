package org.example.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

public class AudioManager {

    private static MediaPlayer backgroundMusic;
    private static boolean musicEnabled;
    private static boolean sfxEnabled;

    public static void configure(boolean musicOn, boolean sfxOn) {
        musicEnabled = musicOn;
        sfxEnabled = sfxOn;
    }

    // Play looping background music
    public static void playMusic(String resourcePath) {
        if (!musicEnabled) return;

        stopMusic();
        URL resource = AudioManager.class.getResource(resourcePath);
        if (resource == null) {
            System.err.println("Music file not found: " + resourcePath);
            return;
        }

        backgroundMusic = new MediaPlayer(new Media(resource.toString()));
        backgroundMusic.setCycleCount(MediaPlayer.INDEFINITE);
        backgroundMusic.setVolume(0.4);
        backgroundMusic.play();
    }

    public static void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.dispose();
            backgroundMusic = null;
        }
    }

    // Play short sound effects
    public static void playSfx(String resourcePath) {
        if (!sfxEnabled) return;

        URL resource = AudioManager.class.getResource(resourcePath);
        if (resource == null) {
            System.err.println("SFX file not found: " + resourcePath);
            return;
        }

        AudioClip clip = new AudioClip(resource.toString());
        clip.play();
    }
}
