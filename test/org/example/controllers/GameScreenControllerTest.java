package org.example.controllers;

import javafx.collections.ObservableList;
import javafx.scene.canvas.Canvas;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class GameScreenControllerTest {

    private GameScreenController controller;

    @BeforeEach
    void setUp() {
        controller = new GameScreenController();
    }

    @Test
    void computeTileSizeToFit_ReturnsCorrectSize() throws Exception {
        Canvas mockCanvas = new Canvas(300, 600);
        int cols = 10;
        int rows = 20;

        Method method = GameScreenController.class.getDeclaredMethod(
                "computeTileSizeToFit", Canvas.class, int.class, int.class);
        method.setAccessible(true);

        int tileSize = (int) method.invoke(controller, mockCanvas, cols, rows);
        assertEquals(30, tileSize);
    }
}