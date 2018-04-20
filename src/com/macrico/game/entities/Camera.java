package com.macrico.game.entities;

import com.macrico.game.renderEngine.DisplayManager;
import com.macrico.game.terrains.Terrain;
import com.macrico.game.toolbox.Input;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.vector.Vector3f;

public class Camera {

    private float distanceFromPlayer = 10;
    private float angleAroundPlayer = 0;

    private Vector3f position = new Vector3f(0, 5, 0);
    private float pitch = 20;
    private float yaw = 0;

    private Player player;
    private Terrain terrain;

    private static final float sensitivity = 15.0f;

    public Camera(Player player, Terrain terrain) {
        this.player = player;
        this.terrain = terrain;
    }

    public void tick() {
        if (Input.isKeyPressed(Keyboard.KEY_TAB)) {
            firstPersonMove();
            Mouse.setGrabbed(true);
        } else {
            move();
            Mouse.setGrabbed(false);
        }

        checkBorders();
    }

    private void checkBorders() {
        if (getPosition().x > -2.0f)
            setPosition(new Vector3f(-2.0f, getPosition().y, getPosition().z));
        else if (getPosition().x < -798.0f)
            setPosition(new Vector3f(-798.0f, getPosition().y, getPosition().z));
        if (getPosition().z > -2.0f)
            setPosition(new Vector3f(getPosition().x, getPosition().y, -2.0f));
        else if (getPosition().z < -798.0f)
            setPosition(new Vector3f(getPosition().x, getPosition().y, -798.0f));
        if (getPosition().y <= (terrain.getHeightOfTerrain(getPosition().x, getPosition().z) + 0.5f))
            setPosition(new Vector3f(getPosition().x, (terrain.getHeightOfTerrain(getPosition().x, getPosition().z) + 0.5f), getPosition().z));
    }

    private void move() {
        calculateZoom();
        calculatePitch();
        calculateAngleAroundPlayer();

        float horizontalDistance = calculateHorizontalDistance();
        float verticalDistance = calculateVerticalDistance();
        calculateCameraPosition(horizontalDistance, verticalDistance);

        this.yaw = 180 - (player.getRotY() - angleAroundPlayer);
    }

    private void firstPersonMove() {
        calculatePosition();
        mouseMove();
        this.yaw = 180 - player.getRotY();
    }

    private void mouseMove() {
        Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
        player.increaseRotation(0, -Mouse.getDX() * sensitivity * DisplayManager.getFrameTimeSeconds(), 0);
        pitch -= Mouse.getDY() * sensitivity * DisplayManager.getFrameTimeSeconds();
        if (pitch <= -90.0f) pitch = -90.0f;
        else if (pitch >= 90.0f) pitch = 90.0f;
    }

    private void forceMove() {
        player.increaseRotation(0, -Mouse.getDX() * sensitivity * DisplayManager.getFrameTimeSeconds(), 0);
    }

    private void calculatePosition() {
        position.x = player.getPosition().x;
        position.z = player.getPosition().z;
        position.y = player.getPosition().y + 2;
    }

    public Vector3f getPosition() {
        return position;
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public float getPitch() {
        return pitch;
    }

    public float getYaw() {
        return yaw;
    }

    private void calculateCameraPosition(float horizontalDistance, float verticalDistance) {
        float theta = player.getRotY() - angleAroundPlayer;
        float offsetX = (float) (horizontalDistance * Math.sin(Math.toRadians(theta)));
        float offsetZ = (float) (horizontalDistance * Math.cos(Math.toRadians(theta)));
        position.x = player.getPosition().x - offsetX;
        position.z = player.getPosition().z - offsetZ;
        position.y = (player.getPosition().y + 2) + verticalDistance;
    }

    private float calculateHorizontalDistance() {
        return (float) (distanceFromPlayer * Math.cos(Math.toRadians(pitch)));
    }

    private float calculateVerticalDistance() {
        return (float) (distanceFromPlayer * Math.sin(Math.toRadians(pitch)));
    }

    private void calculateZoom() {
        float zoomLevel = Mouse.getDWheel() * 0.01f;
        distanceFromPlayer -= zoomLevel;
        if (distanceFromPlayer <= 2.0f) distanceFromPlayer = 2.0f;
        else if (distanceFromPlayer >= 40.0f) distanceFromPlayer = 40.0f;
    }

    private void calculatePitch() {
        if (Mouse.isButtonDown(0) || Mouse.isButtonDown(1)) {
            float pitchChange = Mouse.getDY() * 0.1f;
            pitch -= pitchChange;
            if (pitch <= -45.0f) pitch = -45.0f;
            else if (pitch >= 90.0f) pitch = 90.0f;
        }
    }

    private void calculateAngleAroundPlayer() {
        if (Mouse.isButtonDown(0)) {
            forceMove();
        } else if (Mouse.isButtonDown(1)) {
            float angleChange = Mouse.getDX() * 0.3f;
            angleAroundPlayer += angleChange;
        }
    }

    public void invertPitch() {
        this.pitch = -pitch;
    }
}
