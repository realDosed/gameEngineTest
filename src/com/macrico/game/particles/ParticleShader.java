package com.macrico.game.particles;

import com.macrico.game.shaders.ShaderProgram;
import org.lwjgl.util.vector.Matrix4f;

public class ParticleShader extends ShaderProgram {

    private static final String VERTEX_FILE = "/com/macrico/game/particles/particleVShader.txt";
    private static final String FRAGMENT_FILE = "/com/macrico/game/particles/particleFShader.txt";

    private int location_numberOfRows;
    private int location_projectionMatrix;

    public ParticleShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void getAllUniformLocations() {
        location_numberOfRows = super.getUniformLocation("numberOfRows");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
    }

    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "modelViewMatrix");
        super.bindAttribute(5, "texOffsets");
        super.bindAttribute(6, "blendFactor");
    }

    protected void loadNumberOfRows(float numberOfRows) {
        super.loadFloat(location_numberOfRows, numberOfRows);
    }

    protected void loadProjectionMatrix(Matrix4f projectionMatrix) {
        super.loadMatrix(location_projectionMatrix, projectionMatrix);
    }
}
