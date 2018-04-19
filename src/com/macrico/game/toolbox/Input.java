package com.macrico.game.toolbox;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class Input {

    private static boolean keyPressed = false;
    private static int key = -1;

    private static boolean mousePressed = false;
    private static int mouseButton = -1;

    public static void tick() {
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                if (Keyboard.getEventKey() == key && !keyPressed) {
                    keyPressed = true;
                } else if (Keyboard.getEventKey() == key && keyPressed) {
                    keyPressed = false;
                }
            }
        }

        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                mouseButton = Mouse.getEventButton();
                mousePressed = true;
            }
        }
    }

    public static boolean isKeyPressed(int key) {
        Input.key = key;
        return keyPressed;
    }

    public static boolean isMousePressed(int key) {
        if (mousePressed) {
            return mouseButton == key;
        } else return false;
    }

    public static void setMousePressed(boolean pressed) {
        mousePressed = pressed;
    }
}
