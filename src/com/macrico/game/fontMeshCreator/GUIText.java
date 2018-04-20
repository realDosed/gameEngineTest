package com.macrico.game.fontMeshCreator;

import com.macrico.game.fontRendering.TextMaster;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class GUIText {

    private String textString;
    private float fontSize;

    private int textMeshVao;
    private int vertexCount;
    private Vector3f color = new Vector3f(0f, 0f, 0f);

    private Vector2f position;
    private float lineMaxSize;
    private int numberOfLines;

    private FontType font;

    private boolean centerText;

    private float width = 0.46f;
    private float edge = 0.19f;
    private float borderWidth = 0.0f;
    private float borderEdge = 0.4f;
    private Vector2f offset = new Vector2f(0.0f, 0.0f);
    private Vector3f outlineColor = new Vector3f(0.0f, 0.0f, 0.0f);

    public GUIText(String text, float fontSize, FontType font, Vector2f position, float maxLineLength, boolean centered) {
        this.textString = text;
        this.fontSize = fontSize;
        this.font = font;
        this.position = position;
        this.lineMaxSize = maxLineLength;
        this.centerText = centered;
        TextMaster.loadText(this);
    }

    public void setSettings(float width, float edge, float borderWidth, float borderEdge, Vector2f offset, Vector3f outlineColor) {
        this.width = width;
        this.edge = edge;
        this.borderWidth = borderWidth;
        this.borderEdge = borderEdge;
        this.offset = offset;
        this.outlineColor = outlineColor;
    }

    public void setTextString(String text) {
        this.textString = text;
    }

    public void remove() {
        TextMaster.remove(this);
    }

    public FontType getFont() {
        return font;
    }

    public void setColor(float r, float g, float b) {
        color.set(r, g, b);
    }

    public Vector3f getColor() {
        return color;
    }

    public int getNumberOfLines() {
        return numberOfLines;
    }

    public Vector2f getPosition() {
        return position;
    }

    public int getMesh() {
        return textMeshVao;
    }

    public void setMeshInfo(int vao, int verticesCount) {
        this.textMeshVao = vao;
        this.vertexCount = verticesCount;
    }

    public int getVertexCount() {
        return this.vertexCount;
    }

    protected float getFontSize() {
        return fontSize;
    }


    protected void setNumberOfLines(int number) {
        this.numberOfLines = number;
    }

    protected boolean isCentered() {
        return centerText;
    }

    protected float getMaxLineSize() {
        return lineMaxSize;
    }

    protected String getTextString() {
        return textString;
    }

    public float getFontWidth() {
        return width;
    }

    public float getFontEdge() {
        return edge;
    }

    public float getBorderFontWidth() {
        return borderWidth;
    }

    public float getBorderFontEdge() {
        return borderEdge;
    }

    public Vector2f getFontOffset() {
        return offset;
    }

    public Vector3f getFontOutlineColor() {
        return outlineColor;
    }
}
