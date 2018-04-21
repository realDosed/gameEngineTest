package com.macrico.game.waterTesting;

public class WaterTile {

    private static final float SIZE = 800;

    private float x;
    private float z;
    private float height;

    public WaterTile(int gridX, int gridZ, float height) {
        this.x = gridX * SIZE;
        this.z = gridZ * SIZE;
        this.height = height;
    }

    public float getX() {
        return x;
    }

    public float getZ() {
        return z;
    }

    public float getHeight() {
        return height;
    }
}
