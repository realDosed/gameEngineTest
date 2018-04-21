package com.macrico.game.postProcessing;

import com.macrico.game.entities.Camera;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

public class ContrastChanger {

    private ImageRenderer renderer;
    private ContrastShader shader;

    public ContrastChanger() {
        shader = new ContrastShader();
        renderer = new ImageRenderer();
    }

    public void render(int texture, Camera camera, float waterHeight) {
        shader.start();
        shader.loadCameraPosition(camera);
        shader.loadWaterHeight(waterHeight);
        shader.loadResolution(Display.getWidth(), Display.getHeight());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
        renderer.renderQuad();
        shader.stop();
    }

    public void cleanUp() {
        renderer.cleanUp();
        shader.cleanUp();
    }
}
