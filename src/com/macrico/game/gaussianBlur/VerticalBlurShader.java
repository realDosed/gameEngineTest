package com.macrico.game.gaussianBlur;

import com.macrico.game.shaders.ShaderProgram;

public class VerticalBlurShader extends ShaderProgram {

    private static final String VERTEX_FILE = "/com/macrico/game/gaussianBlur/verticalBlurVertex.txt";
    private static final String FRAGMENT_FILE = "/com/macrico/game/gaussianBlur/blurFragment.txt";

    private int location_targetHeight;

    protected VerticalBlurShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void loadTargetHeight(float height) {
        super.loadFloat(location_targetHeight, height);
    }

    protected void getAllUniformLocations() {
        location_targetHeight = super.getUniformLocation("targetHeight");
    }

    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
