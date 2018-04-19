package com.macrico.game.entities;

import com.macrico.game.renderEngine.MasterRenderer;
import org.lwjgl.util.vector.Vector3f;

public class Sun extends Light {

    private static final float DAY_COLOR = 1.3f;
    private static final float NIGHT_COLOR = 0.0f;

    public Sun(Vector3f position) {
        super(position);
    }

    public void update() {
        float color;
        float color2;
        if (MasterRenderer.isNight()) {
            color = NIGHT_COLOR;
            color2 = NIGHT_COLOR;
        } else if (MasterRenderer.isMorning()) {
            color = NIGHT_COLOR;
            color2 = DAY_COLOR;
        } else if (MasterRenderer.isDay()) {
            color = DAY_COLOR;
            color2 = DAY_COLOR;
        } else if (MasterRenderer.isEvening()) {
            color = DAY_COLOR;
            color2 = NIGHT_COLOR;
        } else {
            color = 0;
            color2 = 0;
        }

        float finalColor = (color * MasterRenderer.getInverseBlendFactor()) + (color2 * MasterRenderer.getBlendFactor());
        setColor(new Vector3f(finalColor, finalColor, finalColor));
    }
}
