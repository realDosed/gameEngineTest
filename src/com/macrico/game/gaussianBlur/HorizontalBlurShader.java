package com.macrico.game.gaussianBlur;

import com.macrico.game.shaders.ShaderProgram;

public class HorizontalBlurShader extends ShaderProgram {

    private static final String VERTEX_FILE = "/com/macrico/game/gaussianBlur/horizontalBlurVertex.txt";
    private static final String FRAGMENT_FILE = "/com/macrico/game/gaussianBlur/blurFragment.txt";

    private int location_targetWidth;

    protected HorizontalBlurShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void loadTargetWidth(float width) {
        super.loadFloat(location_targetWidth, width);
    }

    protected void getAllUniformLocations() {
        location_targetWidth = super.getUniformLocation("targetWidth");
    }

    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }
}
