package com.macrico.game.shadows;

import java.util.List;
import java.util.Map;

import com.macrico.game.entities.Entity;
import com.macrico.game.models.RawModel;
import com.macrico.game.models.TexturedModel;
import com.macrico.game.renderEngine.MasterRenderer;
import com.macrico.game.toolbox.Maths;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;

public class ShadowMapEntityRenderer {

    private Matrix4f projectionViewMatrix;
    private ShadowShader shader;

    protected ShadowMapEntityRenderer(ShadowShader shader, Matrix4f projectionViewMatrix) {
        this.shader = shader;
        this.projectionViewMatrix = projectionViewMatrix;
    }

    protected void render(Map<TexturedModel, List<Entity>> entities) {
        for (TexturedModel model : entities.keySet()) {
            RawModel rawModel = model.getRawModel();
            bindModel(rawModel);
            GL13.glActiveTexture(GL13.GL_TEXTURE0);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getID());
            if (model.getTexture().isHasTransparency()) {
                MasterRenderer.disableCulling();
            }
            for (Entity entity : entities.get(model)) {
                prepareInstance(entity);
                GL11.glDrawElements(GL11.GL_TRIANGLES, rawModel.getVertexCount(), GL11.GL_UNSIGNED_INT, 0);
            }
            if (model.getTexture().isHasTransparency()) {
                MasterRenderer.enableCulling();
            }
        }
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL30.glBindVertexArray(0);
    }

    private void bindModel(RawModel rawModel) {
        GL30.glBindVertexArray(rawModel.getVaoID());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
    }

    private void prepareInstance(Entity entity) {
        Matrix4f modelMatrix = Maths.createTransformationMatrix(entity.getPosition(), entity.getRotX(), entity.getRotY(), entity.getRotZ(), entity.getScale());
        Matrix4f mvpMatrix = Matrix4f.mul(projectionViewMatrix, modelMatrix, null);
        shader.loadMvpMatrix(mvpMatrix);
    }
}
