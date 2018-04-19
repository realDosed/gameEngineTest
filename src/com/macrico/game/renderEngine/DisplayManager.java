package com.macrico.game.renderEngine;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.*;

public class DisplayManager {

    private static final int WIDTH = 1280;
    private static final int HEIGHT = 720;
    private static final int FPS_CAP = 60;

    private static long lastFrameTime;
    public static float delta;
    private static float fps, lastFPS;

    private static int realFps = 0;
    public static boolean canUpdate;

    public static void createDisplay() {
        ContextAttribs attribs = new ContextAttribs(3, 3).withForwardCompatible(true).withProfileCore(true);

        try {
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.create(new PixelFormat(), attribs);
        } catch (LWJGLException e) {
            e.printStackTrace();
        }

        GL11.glViewport(0, 0, WIDTH, HEIGHT);

        getDelta();
        lastFPS = getCurrentTime();
    }

    public static void updateDisplay() {
        Display.update();
        Display.sync(FPS_CAP);
    }

    public static void updateFPS() {
        if (getCurrentTime() - lastFPS > 1000) {
            realFps = (int) fps;
            canUpdate = true;
            fps = 0;
            lastFPS += 1000;
        } else canUpdate = false;
        fps++;
    }

    public static float getDelta() {
        long currentFrameTime = getCurrentTime();
        float delta = (currentFrameTime - lastFrameTime) / 1000f;
        lastFrameTime = currentFrameTime;

        return delta;
    }

    public static int getFps() {
        return realFps;
    }

    private static long getCurrentTime() {
        return (Sys.getTime() * 1000) / Sys.getTimerResolution();
    }

    public static float getFrameTimeSeconds() {
        return delta;
    }

    public static void closeDisplay() {
        Display.destroy();
    }
}
