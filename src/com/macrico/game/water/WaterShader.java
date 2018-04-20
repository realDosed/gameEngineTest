package com.macrico.game.water;

import com.macrico.game.entities.Camera;
import com.macrico.game.entities.Light;
import com.macrico.game.shaders.ShaderProgram;
import com.macrico.game.toolbox.Maths;
import org.lwjgl.util.vector.Matrix4f;

public class WaterShader extends ShaderProgram {

    private final static String VERTEX_FILE = "src/com/macrico/game/water/waterVertex.txt";
    private final static String FRAGMENT_FILE = "src/com/macrico/game/water/waterFragment.txt";

    private int location_modelMatrix;
    private int location_viewMatrix;
    private int location_projectionMatrix;
    private int location_reflectionTexture;
    private int location_refractionTexture;
    private int location_dudvMap;
    private int location_moveFactor;
    private int location_cameraPosition;
    private int location_normalMap;
    private int location_lightPosition;
    private int location_lightColor;
    private int location_depthMap;

    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void bindAttributes() {
        bindAttribute(0, "position");
    }

    protected void getAllUniformLocations() {
        location_projectionMatrix = getUniformLocation("projectionMatrix");
        location_viewMatrix = getUniformLocation("viewMatrix");
        location_modelMatrix = getUniformLocation("modelMatrix");
        location_reflectionTexture = getUniformLocation("reflectionTexture");
        location_refractionTexture = getUniformLocation("refractionTexture");
        location_dudvMap = getUniformLocation("dudvMap");
        location_moveFactor = getUniformLocation("moveFactor");
        location_cameraPosition = getUniformLocation("cameraPosition");
        location_normalMap = getUniformLocation("normalMap");
        location_lightPosition = getUniformLocation("lightPosition");
        location_lightColor = getUniformLocation("lightColor");
        location_depthMap = getUniformLocation("depthMap");
    }

    public void connectTextureUnits() {
        super.loadInt(location_reflectionTexture, 0);
        super.loadInt(location_refractionTexture, 1);
        super.loadInt(location_dudvMap, 2);
        super.loadInt(location_normalMap, 3);
        super.loadInt(location_depthMap, 4);
    }

    public void loadLight(Light sun) {
        super.loadVector(location_lightColor, sun.getColor());
        super.loadVector(location_lightPosition, sun.getPosition());
    }

    public void loadMoveFactor(float factor) {
        super.loadFloat(location_moveFactor, factor);
    }

    public void loadProjectionMatrix(Matrix4f projection) {
        loadMatrix(location_projectionMatrix, projection);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        loadMatrix(location_viewMatrix, viewMatrix);
        super.loadVector(location_cameraPosition, camera.getPosition());
    }

    public void loadModelMatrix(Matrix4f modelMatrix) {
        loadMatrix(location_modelMatrix, modelMatrix);
    }
}
