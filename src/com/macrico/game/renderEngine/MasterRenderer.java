package com.macrico.game.renderEngine;

import com.macrico.game.engineTester.MainGameLoop;
import com.macrico.game.entities.*;
import com.macrico.game.models.TexturedModel;
import com.macrico.game.normalMappingRenderer.NormalMappingRenderer;
import com.macrico.game.shaders.StaticShader;
import com.macrico.game.shadows.ShadowMapMasterRenderer;
import com.macrico.game.terrains.TerrainRenderer;
import com.macrico.game.terrains.TerrainShader;
import com.macrico.game.skybox.SkyboxRenderer;
import com.macrico.game.terrains.Terrain;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector4f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MasterRenderer {

    public static final float FOV = 70;
    public static final float NEAR_PLANE = 0.1f;
    public static final float FAR_PLANE = 1000;

    private static final float RED = 0.494f; //0.5444f;
    private static final float GREEN = 0.803f; //0.62f;
    private static final float BLUE = 0.862f; //0.69f;
    private static final float NIGHT_RED = 0.05f;
    private static final float NIGHT_GREEN = 0.05f;
    private static final float NIGHT_BLUE = 0.05f;

    public static float FINAL_RED;
    public static float FINAL_GREEN;
    public static float FINAL_BLUE;

    private Matrix4f projectionMatrix;

    private StaticShader shader = new StaticShader();
    private EntityRenderer renderer;

    private TerrainRenderer terrainRenderer;
    private TerrainShader terrainShader = new TerrainShader();

    private Map<TexturedModel, List<Entity>> entities = new HashMap<>();
    private Map<TexturedModel, List<Entity>> normalEntities = new HashMap<>();
    private List<Terrain> terrains = new ArrayList<>();

    private NormalMappingRenderer normalRenderer;
    private SkyboxRenderer skyboxRenderer;
    private ShadowMapMasterRenderer shadowMapRenderer;

    private static float time = MainGameLoop.START_TIME;
    private static float blendFactor = 0, inverseBlendFactor = 0;

    private static boolean night = false;
    private static boolean morning = false;
    private static boolean day = true;
    private static boolean evening = false;

    public MasterRenderer(Loader loader, Camera cam) {
        enableCulling();

        createProjectionMatrix();
        renderer = new EntityRenderer(shader, projectionMatrix);
        terrainRenderer = new TerrainRenderer(terrainShader, projectionMatrix);
        skyboxRenderer = new SkyboxRenderer(loader, projectionMatrix);
        normalRenderer = new NormalMappingRenderer(projectionMatrix);
        shadowMapRenderer = new ShadowMapMasterRenderer(cam);
    }

    public void renderScene(List<Entity> entities, List<Entity> normalMapEntities, List<Lamp> lamps, List<Light> lights, List<Terrain> terrains, Camera camera, Vector4f clipPlane) {
        for (Terrain terrain : terrains) processTerrain(terrain);
        for (Entity entity : entities) processEntity(entity);
        for (Entity normalEntity : normalMapEntities) processNormalMapEntity(normalEntity);
        for (Lamp lamp : lamps) processEntity(lamp);
        render(lights, camera, clipPlane);
    }

    public Matrix4f getProjectionMatrix() {
        return projectionMatrix;
    }

    public static void enableCulling() {
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public static void disableCulling() {
        GL11.glDisable(GL11.GL_CULL_FACE);
    }

    public void render(List<Light> lights, Camera camera, Vector4f clipPlane) {
        updateColor();
        prepare();

        shader.start();
        shader.loadClipPlane(clipPlane);
        shader.loadSkyColor(FINAL_RED, FINAL_GREEN, FINAL_BLUE);
        shader.loadLights(lights);
        shader.loadViewMatrix(camera);
        renderer.render(entities, shadowMapRenderer.getToShadowMapSpaceMatrix());
        shader.stop();

        normalRenderer.render(normalEntities, clipPlane, lights, camera);

        terrainShader.start();
        terrainShader.loadClipPlane(clipPlane);
        terrainShader.loadSkyColor(FINAL_RED, FINAL_GREEN, FINAL_BLUE);
        terrainShader.loadLights(lights);
        terrainShader.loadViewMatrix(camera);
        terrainRenderer.render(terrains, shadowMapRenderer.getToShadowMapSpaceMatrix());
        terrainShader.stop();

        skyboxRenderer.render(camera, FINAL_RED, FINAL_GREEN, FINAL_BLUE);

        terrains.clear();
        entities.clear();
        normalEntities.clear();
    }

    private void processTerrain(Terrain terrain) {
        terrains.add(terrain);
    }

    private void processEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = entities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            entities.put(entityModel, newBatch);
        }
    }

    private void processNormalMapEntity(Entity entity) {
        TexturedModel entityModel = entity.getModel();
        List<Entity> batch = normalEntities.get(entityModel);
        if (batch != null) {
            batch.add(entity);
        } else {
            List<Entity> newBatch = new ArrayList<>();
            newBatch.add(entity);
            normalEntities.put(entityModel, newBatch);
        }
    }

    public void renderShadowMap(List<Entity> entityList, Light sun) {
        for (Entity entity : entityList) {
            processEntity(entity);
        }
        shadowMapRenderer.render(entities, sun);
        entities.clear();
    }

    private int getShadowMapTexture() {
        return shadowMapRenderer.getShadowMap();
    }

    public void cleanUp() {
        shader.cleanUp();
        terrainShader.cleanUp();
        normalRenderer.cleanUp();
        shadowMapRenderer.cleanUp();
    }

    private void prepare() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glClearColor(FINAL_RED, FINAL_GREEN, FINAL_BLUE, 1);
        GL13.glActiveTexture(GL13.GL_TEXTURE5);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, getShadowMapTexture());
    }

    private void createProjectionMatrix() {
        projectionMatrix = new Matrix4f();
        float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
        float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = FAR_PLANE - NEAR_PLANE;

        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
        projectionMatrix.m33 = 0;
    }

    private void updateColor() {
        float red, red2;
        float green, green2;
        float blue, blue2;
        if (isNight()) {
            red = NIGHT_RED;
            red2 = NIGHT_RED;
            green = NIGHT_GREEN;
            green2 = NIGHT_GREEN;
            blue = NIGHT_BLUE;
            blue2 = NIGHT_BLUE;
        } else if (isMorning()) {
            red = NIGHT_RED;
            red2 = RED;
            green = NIGHT_GREEN;
            green2 = GREEN;
            blue = NIGHT_BLUE;
            blue2 = BLUE;
        } else if (isDay()) {
            red = RED;
            red2 = RED;
            green = GREEN;
            green2 = GREEN;
            blue = BLUE;
            blue2 = BLUE;
        } else if (isEvening()) {
            red = RED;
            red2 = NIGHT_RED;
            green = GREEN;
            green2 = NIGHT_GREEN;
            blue = BLUE;
            blue2 = NIGHT_BLUE;
        } else {
            red = 0;
            red2 = 0;
            green = 0;
            green2 = 0;
            blue = 0;
            blue2 = 0;
        }

        FINAL_RED = (red * getInverseBlendFactor()) + (red2 * getBlendFactor());
        FINAL_GREEN = (green * getInverseBlendFactor()) + (green2 * getBlendFactor());
        FINAL_BLUE = (blue * getInverseBlendFactor()) + (blue2 * getBlendFactor());
    }

    public void tick() {
        time += DisplayManager.getFrameTimeSeconds() * 10;
        time %= 2400;
        if (time >= 0 && time < 900) {
            night = true;
            morning = false;
            day = false;
            evening = false;

            blendFactor = (time - 0) / (900);
        } else if (time >= 900 && time < 1200) {
            night = false;
            morning = true;
            day = false;
            evening = false;

            blendFactor = (time - 900) / (1200 - 900);
        } else if (time >= 1200 && time < 2100) {
            night = false;
            morning = false;
            day = true;
            evening = false;

            blendFactor = (time - 1200) / (2100 - 1200);
        } else {
            night = false;
            morning = false;
            day = false;
            evening = true;

            blendFactor = (time - 2100) / (2400 - 2100);
        }
        inverseBlendFactor = 1.0f - blendFactor;
    }

    public static boolean isNight() {
        return night;
    }

    public static boolean isMorning() {
        return morning;
    }

    public static boolean isDay() {
        return day;
    }

    public static boolean isEvening() {
        return evening;
    }

    public static float getBlendFactor() {
        return blendFactor;
    }

    public static float getInverseBlendFactor() {
        return inverseBlendFactor;
    }
}
