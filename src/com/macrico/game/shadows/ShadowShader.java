package com.macrico.game.shadows;

import com.macrico.game.shaders.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;

public class ShadowShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/com/macrico/game/shadows/shadowVertexShader.txt";
    private static final String FRAGMENT_FILE = "src/com/macrico/game/shadows/shadowFragmentShader.txt";

    private int location_mvpMatrix;

    protected ShadowShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void getAllUniformLocations() {
        location_mvpMatrix = super.getUniformLocation("mvpMatrix");

    }

    protected void loadMvpMatrix(Matrix4f mvpMatrix) {
        super.loadMatrix(location_mvpMatrix, mvpMatrix);
    }

    protected void bindAttributes() {
        super.bindAttribute(0, "in_position");
        super.bindAttribute(1, "in_textureCoords");
    }
}
