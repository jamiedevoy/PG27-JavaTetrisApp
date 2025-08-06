package org.example.enums;

public enum TetrominoType {
    I(new int[][]{{1, 1, 1, 1}}, 1),
    O(new int[][]{{2, 2}, {2, 2}}, 2),
    T(new int[][]{{0, 3, 0}, {3, 3, 3}}, 3),
    S(new int[][]{{0, 4, 4}, {4, 4, 0}}, 4),
    Z(new int[][]{{5, 5, 0}, {0, 5, 5}}, 5),
    J(new int[][]{{6, 0, 0}, {6, 6, 6}}, 6),
    L(new int[][]{{0, 0, 7}, {7, 7, 7}}, 7);

    public final int[][] shape;
    public final int colorIndex;

    TetrominoType(int[][] shape, int colorIndex) {
        this.shape = shape;
        this.colorIndex = colorIndex;
    }
}
