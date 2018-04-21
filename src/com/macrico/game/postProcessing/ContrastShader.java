package com.macrico.game.postProcessing;

import com.macrico.game.entities.Camera;
import com.macrico.game.shaders.ShaderProgram;
import org.lwjgl.util.vector.Vector2f;

public class ContrastShader extends ShaderProgram {

    private static final String VERTEX_FILE = "/com/macrico/game/postProcessing/contrastVertex.txt";
    private static final String FRAGMENT_FILE = "/com/macrico/game/postProcessing/contrastFragment.txt";

    private int location_cameraPosition;
    private int location_waterHeight;
    private int location_resolution;

    public ContrastShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void getAllUniformLocations() {
        location_cameraPosition = super.getUniformLocation("cameraPosition");
        location_waterHeight = super.getUniformLocation("waterHeight");
        location_resolution = super.getUniformLocation("resolution");
    }

    protected void bindAttributes() {
        super.bindAttribute(0, "position");
    }

    public void loadCameraPosition(Camera camera) {
        super.loadVector(location_cameraPosition, camera.getPosition());
    }

    public void loadWaterHeight(float height) {
        super.loadFloat(location_waterHeight, height);
    }

    public void loadResolution(float width, float height) {
        super.load2DVector(location_resolution, new Vector2f(width, height));
    }
}
