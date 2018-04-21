package com.macrico.game.waterTesting;

import com.macrico.game.entities.Camera;
import com.macrico.game.entities.Light;
import com.macrico.game.models.RawModel;
import com.macrico.game.renderEngine.DisplayManager;
import com.macrico.game.renderEngine.Loader;
import com.macrico.game.renderEngine.MasterRenderer;
import com.macrico.game.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class WaterRendererTest {

    private static final String DUDV_MAP = "textures/waterDUDV";
    private static final String NORMAL_MAP = "textures/matchingNormalMap";
    private static final float WAVE_SPEED = 0.03f;

    private static final int VERTEX_COUNT = 256;

    private RawModel quad;
    private WaterShader shader;
    private WaterFrameBuffers frameBuffers;

    private float moveFactor = 0;

    private int dudvTexture;
    private int normalMapTexture;

    public WaterRendererTest(Loader loader, Matrix4f projectionMatrix, WaterFrameBuffers frameBuffers) {
        this.shader = new WaterShader();
        this.frameBuffers = frameBuffers;
        dudvTexture = loader.loadTexture(DUDV_MAP);
        normalMapTexture = loader.loadTexture(NORMAL_MAP);
        shader.start();
        shader.connectTextureUnits();
        shader.loadProjectionMatrix(projectionMatrix);
        shader.stop();
        generateQuad(loader);
    }

    public void render(WaterTile tile, Camera camera, Light sun) {
        prepare(camera, sun);
        Matrix4f modelMatrix = Maths.createTransformationMatrix(new Vector3f(tile.getX(), tile.getHeight(), tile.getZ()), 0, 0, 0, 1);
        shader.loadTransformationMatrix(modelMatrix);
        GL11.glDrawElements(GL11.GL_TRIANGLES, quad.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
        unbind();
    }

    public void cleanUp() {
        shader.cleanUp();
    }

    private void prepare(Camera camera, Light sun) {
        shader.start();
        shader.loadViewMatrix(camera);

        moveFactor += WAVE_SPEED * DisplayManager.getFrameTimeSeconds();
        moveFactor %= 1;

        shader.loadMoveFactor(moveFactor);
        shader.loadLight(sun);
        shader.loadSkyColor(MasterRenderer.FINAL_RED, MasterRenderer.FINAL_GREEN, MasterRenderer.FINAL_BLUE);

        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);

        MasterRenderer.disableCulling();

        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, frameBuffers.getReflectionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, frameBuffers.getRefractionTexture());
        GL13.glActiveTexture(GL13.GL_TEXTURE2);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, dudvTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE3);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, normalMapTexture);
        GL13.glActiveTexture(GL13.GL_TEXTURE4);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, frameBuffers.getRefractionDepthTexture());

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    private void unbind() {
        MasterRenderer.enableCulling();

        GL11.glDisable(GL11.GL_BLEND);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
        shader.stop();
    }

    private void generateQuad(Loader loader) {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 2];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];

        int vertexPointer = 0;
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1) * WaterTile.SIZE;
                vertices[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1) * WaterTile.SIZE;

                textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
                textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
                vertexPointer++;
            }
        }

        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }

        quad = loader.loadToVAO(vertices, textureCoords, indices);
    }
}
