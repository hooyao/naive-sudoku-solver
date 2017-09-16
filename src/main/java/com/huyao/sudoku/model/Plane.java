package com.huyao.sudoku.model;

import java.util.Arrays;

public class Plane {
    public static final int WIDTH = 3;
    public static final int HEIGHT = 3;

    private final int[][] data = new int[WIDTH * HEIGHT][];

    public Plane() {

    }

    public int[] get(int x, int y) {
        return this.data[y * WIDTH + x];
    }

    public void set(int x, int y, int[] grid) {
        this.data[y * WIDTH + x] = null;
        this.data[y * WIDTH + x] = grid;
    }

    public Plane clone() {
        Plane p = new Plane();
        for (int i = 0; i < WIDTH; ++i) {
            for (int j = 0; j < HEIGHT; ++j) {
                p.set(i, j, this.get(i, j));
            }
        }
        return p;
    }

    public int getGridCount() {
        int count = 0;
        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i] != null)
                ++count;
        }
        return count;
    }

    public int computeHash() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            sb.append(Arrays.toString(this.data[i]));
        }
        return sb.toString().hashCode();
    }
}
