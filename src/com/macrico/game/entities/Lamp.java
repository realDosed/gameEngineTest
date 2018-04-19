package com.macrico.game.entities;

import com.macrico.game.models.TexturedModel;
import org.lwjgl.util.vector.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Lamp extends Entity {

    private Vector3f position;
    private Vector3f attenuation = new Vector3f(1, 0.01f, 0.002f);
    private List<Light> lights;
    private Vector3f color = new Vector3f(2, 2, 0);

    public Lamp(List<Light> lights, TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);

        this.position = position;
        this.lights = lights;
        setLight();
    }

    private void setLight() {
        lights.add(new Light(new Vector3f(position.x, position.y + 10, position.z), color, attenuation));
    }
}
