package org.example.AI;

public class AIMove {
    private final int column;
    private final int rotation;

    public AIMove(int column, int rotation) {
        this.column = column;
        this.rotation = rotation;
    }

    public int getColumn() {
        return column;
    }

    public int getRotation() {
        return rotation;
    }
}