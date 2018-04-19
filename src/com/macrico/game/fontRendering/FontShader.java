package com.macrico.game.fontRendering;

import com.macrico.game.shaders.ShaderProgram;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class FontShader extends ShaderProgram {

    private static final String VERTEX_FILE = "src/com/macrico/game/fontRendering/fontVertex.txt";
    private static final String FRAGMENT_FILE = "src/com/macrico/game/fontRendering/fontFragment.txt";

    private int location_translation;
    private int location_color;
    private int location_width;
    private int location_edge;
    private int location_borderWidth;
    private int location_borderEdge;
    private int location_offset;
    private int location_outlineColor;

    public FontShader() {
        super(VERTEX_FILE, FRAGMENT_FILE);
    }

    protected void getAllUniformLocations() {
        location_translation = getUniformLocation("translation");
        location_color = getUniformLocation("color");
        location_width = getUniformLocation("width");
        location_edge = getUniformLocation("edge");
        location_borderWidth = getUniformLocation("borderWidth");
        location_borderEdge = getUniformLocation("borderEdge");
        location_offset = getUniformLocation("offset");
        location_outlineColor = getUniformLocation("outlineColor");
    }

    protected void bindAttributes() {
        super.bindAttribute(0, "position");
        super.bindAttribute(1, "textureCoords");
    }

    protected void loadFontSettings(float width, float edge, float borderWidth, float borderEdge, Vector2f offset, Vector3f outlineColor) {
        super.loadFloat(location_width, width);
        super.loadFloat(location_edge, edge);
        super.loadFloat(location_borderWidth, borderWidth);
        super.loadFloat(location_borderEdge, borderEdge);
        super.load2DVector(location_offset, offset);
        super.loadVector(location_outlineColor, outlineColor);
    }

    protected void loadColor(Vector3f color) {
        super.loadVector(location_color, color);
    }

    protected void loadTranslation(Vector2f translation) {
        super.load2DVector(location_translation, translation);
    }
}
