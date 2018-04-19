package com.macrico.game.entities;

import com.macrico.game.models.TexturedModel;
import com.macrico.game.renderEngine.DisplayManager;
import com.macrico.game.terrains.Terrain;
import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;

public class Player extends Entity {

    public static float RUN_SPEED = 15;
    private static final float TURN_SPEED = 160;
    public static float GRAVITY = -50;
    public final static float REAL_GRAVITY = -50;
    public static float JUMP_POWER = 15;

    private float upwardSpeed = 0;
    private boolean isInAir = false;

    public static boolean underWater = true;

    public Player(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
    }

    public void move(Terrain terrain) {
        checkInputs();
        checkRotation();

        upwardSpeed += GRAVITY * DisplayManager.getFrameTimeSeconds();
        super.increasePosition(0, upwardSpeed * DisplayManager.getFrameTimeSeconds(), 0);

        float terrainHeight = terrain.getHeightOfTerrain(super.getPosition().x, super.getPosition().z);
        if (super.getPosition().y < terrainHeight) {
            upwardSpeed = 0;
            isInAir = false;
            super.getPosition().y = terrainHeight;
        }

        checkBorders();
    }

    private void checkBorders() {
        if (super.getPosition().x > -2.0f)
            super.setPosition(new Vector3f(-2.0f, super.getPosition().y, super.getPosition().z));
        else if (super.getPosition().x < -798.0f)
            super.setPosition(new Vector3f(-798.0f, super.getPosition().y, super.getPosition().z));
        if (super.getPosition().z > -2.0f)
            super.setPosition(new Vector3f(super.getPosition().x, super.getPosition().y, -2.0f));
        else if (super.getPosition().z < -798.0f)
            super.setPosition(new Vector3f(super.getPosition().x, super.getPosition().y, -798.0f));
    }

    private void checkInputs() {
        float currentSpeed = RUN_SPEED * DisplayManager.getFrameTimeSeconds();

        if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
            float toX = (float) Math.sin(Math.toRadians(super.getRotY()));
            float toZ = (float) Math.cos(Math.toRadians(super.getRotY()));
            super.increasePosition(toX * currentSpeed, 0, toZ * currentSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
            float toX = (float) Math.sin(Math.toRadians(super.getRotY()));
            float toZ = (float) Math.cos(Math.toRadians(super.getRotY()));
            super.decreasePosition(toX * currentSpeed, 0, toZ * currentSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
            float toX = (float) Math.sin(Math.toRadians(super.getRotY() + 90));
            float toZ = (float) Math.cos(Math.toRadians(super.getRotY() + 90));
            super.decreasePosition(toX * currentSpeed, 0, toZ * currentSpeed);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
            float toX = (float) Math.sin(Math.toRadians(super.getRotY() + 90));
            float toZ = (float) Math.cos(Math.toRadians(super.getRotY() + 90));
            super.increasePosition(toX * currentSpeed, 0, toZ * currentSpeed);
        }

        if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
            if (underWater) {
                underWaterJump();
            } else jump();
        }
    }

    private void checkRotation() {
        float currentTurnSpeed = TURN_SPEED * DisplayManager.getFrameTimeSeconds();

        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            super.decreaseRotation(0, currentTurnSpeed, 0);
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            super.increaseRotation(0, currentTurnSpeed, 0);
        }
    }

    private void jump() {
        if (!isInAir) {
            this.upwardSpeed = JUMP_POWER;
            isInAir = true;
        }
    }

    private void underWaterJump() {
        this.upwardSpeed = JUMP_POWER;
    }
}
