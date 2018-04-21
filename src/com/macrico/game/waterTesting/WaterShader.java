package com.macrico.game.waterTesting;

import com.macrico.game.entities.Camera;
import com.macrico.game.entities.Light;
import com.macrico.game.shaders.ShaderProgram;
import com.macrico.game.toolbox.Maths;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class WaterShader extends ShaderProgram {

    private static final String VERTEX_FILE = "/com/macrico/game/waterTesting/waterVShader.txt";
    private static final String FRAGMENT_FILE = "/com/macrico/game/waterTesting/waterFShader.txt";

    private int location_transformationMatrix;
    private int location_projectionMatrix;
    private int location_viewMatrix;
    private int location_reflectionTexture;
    private int location_refractionTexture;
    private int location_dudvMap;
    private int location_moveFactor;
    private int location_cameraPosition;
    private int location_normalMap;
    private int location_lightPosition;
    private int location_lightColor;
    private int location_depthMap;
    private int location_skyColor;

    public WaterShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void getAllUniformLocations() {
        location_transformationMatrix = super.getUniformLocation("transformationMatrix");
        location_projectionMatrix = super.getUniformLocation("projectionMatrix");
        location_viewMatrix = super.getUniformLocation("viewMatrix");
        location_reflectionTexture = getUniformLocation("reflectionTexture");
        location_refractionTexture = getUniformLocation("refractionTexture");
        location_dudvMap = getUniformLocation("dudvMap");
        location_moveFactor = getUniformLocation("moveFactor");
        location_cameraPosition = getUniformLocation("cameraPosition");
        location_normalMap = getUniformLocation("normalMap");
        location_lightPosition = getUniformLocation("lightPosition");
        location_lightColor = getUniformLocation("lightColor");
        location_depthMap = getUniformLocation("depthMap");
        location_skyColor = super.getUniformLocation("skyColor");
    }

    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "in_textureCoords");
    }

    public void connectTextureUnits() {
        super.loadInt(location_reflectionTexture, 0);
        super.loadInt(location_refractionTexture, 1);
        super.loadInt(location_dudvMap, 2);
        super.loadInt(location_normalMap, 3);
        super.loadInt(location_depthMap, 4);
    }

    public void loadTransformationMatrix(Matrix4f matrix) {
        super.loadMatrix(location_transformationMatrix, matrix);
    }

    public void loadProjectionMatrix(Matrix4f matrix) {
        super.loadMatrix(location_projectionMatrix, matrix);
    }

    public void loadViewMatrix(Camera camera) {
        Matrix4f viewMatrix = Maths.createViewMatrix(camera);
        super.loadMatrix(location_viewMatrix, viewMatrix);
        super.loadVector(location_cameraPosition, camera.getPosition());
    }

    public void loadLight(Light sun) {
        super.loadVector(location_lightColor, sun.getColor());
        super.loadVector(location_lightPosition, sun.getPosition());
    }

    public void loadMoveFactor(float factor) {
        super.loadFloat(location_moveFactor, factor);
    }

    public void loadSkyColor(float r, float g, float b) {
        super.loadVector(location_skyColor, new Vector3f(r, g, b));
    }
}
