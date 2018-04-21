package com.macrico.game.postProcessing;

import com.macrico.game.entities.Camera;
import com.macrico.game.gaussianBlur.HorizontalBlur;
import com.macrico.game.gaussianBlur.VerticalBlur;
import com.macrico.game.models.RawModel;
import com.macrico.game.renderEngine.Loader;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class PostProcessing {

    private static final float[] POSITIONS = {-1, 1, -1, -1, 1, 1, 1, -1};
    private static RawModel quad;
    private static ContrastChanger contrastChanger;
    private static HorizontalBlur horizontalBlur;
    private static VerticalBlur verticalBlur;

    public static void init(Loader loader) {
        quad = loader.loadToVAO(POSITIONS, 2);
        contrastChanger = new ContrastChanger();
        horizontalBlur = new HorizontalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
        verticalBlur = new VerticalBlur(Display.getWidth() / 8, Display.getHeight() / 8);
    }

    public static void doPostProcessing(int colourTexture, Camera camera, float waterHeight) {
        start();
        //horizontalBlur.render(colourTexture);
        //verticalBlur.render(horizontalBlur.getOutputTexture());
        contrastChanger.render(colourTexture, camera, waterHeight);
        end();
    }

    public static void cleanUp() {
        contrastChanger.cleanUp();
        horizontalBlur.cleanUp();
        verticalBlur.cleanUp();
    }

    private static void start() {
        GL30.glBindVertexArray(quad.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
    }

    private static void end() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL20.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
    }
}
