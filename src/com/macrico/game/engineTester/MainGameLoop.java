package com.macrico.game.engineTester;

import com.macrico.game.entities.*;
import com.macrico.game.fontMeshCreator.FontType;
import com.macrico.game.fontMeshCreator.GUIText;
import com.macrico.game.fontRendering.TextMaster;
import com.macrico.game.guis.GuiRenderer;
import com.macrico.game.guis.GuiTexture;
import com.macrico.game.models.TexturedModel;
import com.macrico.game.normalMappingObjConverter.NormalMappedObjLoader;
import com.macrico.game.objConverter.ModelData;
import com.macrico.game.objConverter.OBJFileLoader;
import com.macrico.game.objConverter.OBJLoader;
import com.macrico.game.particles.Particle;
import com.macrico.game.particles.ParticleMaster;
import com.macrico.game.particles.ParticleSystem;
import com.macrico.game.particles.ParticleTexture;
import com.macrico.game.renderEngine.DisplayManager;
import com.macrico.game.renderEngine.Loader;
import com.macrico.game.renderEngine.MasterRenderer;
import com.macrico.game.terrains.Terrain;
import com.macrico.game.textures.ModelTexture;
import com.macrico.game.textures.TerrainTexture;
import com.macrico.game.textures.TerrainTexturePack;
import com.macrico.game.toolbox.Input;
import com.macrico.game.toolbox.MousePicker;
import com.macrico.game.water.WaterFrameBuffers;
import com.macrico.game.water.WaterRenderer;
import com.macrico.game.water.WaterShader;
import com.macrico.game.water.WaterTile;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainGameLoop {

    public static final float START_TIME = 1200;
    private Random random = new Random();

    private Loader loader;

    private List<Terrain> terrains;
    private List<Entity> entities;
    private List<Entity> normalEntities;
    private List<Lamp> lamps;
    private List<Light> lights;
    private List<GuiTexture> guiTextures;
    private List<WaterTile> waters;

    private TerrainTexture backgroundTexture, rTexture, gTexture, bTexture, blendMap;
    private TerrainTexturePack texturePack;
    private TexturedModel tree1Texture, tree2Texture, fernTexture, lampTexture, playerTexture, boulderTexture;

    private Player player;
    private Camera camera;
    private Sun sun;
    private MousePicker picker;

    private MasterRenderer renderer;
    private GuiRenderer guiRenderer;
    private ParticleSystem particleSystem;

    private WaterFrameBuffers waterFrameBuffers;
    private WaterShader waterShader;
    private WaterRenderer waterRenderer;

    private GuiTexture waterVision;

    private void init() {
        DisplayManager.createDisplay();
        loader = new Loader();

        setTextures();
        terrains = new ArrayList<Terrain>();
        terrains.add(new Terrain(-1, -1, loader, texturePack, blendMap));

        float x = random.nextFloat() * -800;
        float z = random.nextFloat() * -800;
        float y = terrains.get(0).getHeightOfTerrain(x, z);
        if (y > -4.0) player = new Player(playerTexture, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.2f);
        else player = new Player(playerTexture, new Vector3f(-200, 0, -200), 0, random.nextFloat() * 360, 0, 0.2f);
        camera = new Camera(player, terrains.get(0));

        renderer = new MasterRenderer(loader, camera);
        guiRenderer = new GuiRenderer(loader);
        picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains.get(0));
        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        TextMaster.init(loader);

        entities = new ArrayList<Entity>();
        normalEntities = new ArrayList<Entity>();
        lamps = new ArrayList<Lamp>();
        lights = new ArrayList<Light>();
        guiTextures = new ArrayList<GuiTexture>();
        waters = new ArrayList<WaterTile>();

        sun = new Sun(new Vector3f(10000, 15000, -10000));
        lights.add(sun);
        setEntities();

        guiTextures.add(new GuiTexture(loader.loadTexture("textures/pixels/vignetteTest"), new Vector2f(0, 0), new Vector2f(1, 1)));
        for (int i = 0; i < 5; i++) {
            guiTextures.add(new GuiTexture(loader.loadTexture("textures/pixels/heart"), new Vector2f(-0.93f + (0.065f * i), 0.90f), new Vector2f(0.03f, 0.05f)));
        }

        setWaters();
        setParticles();

        waterVision = new GuiTexture(loader.loadTexture("textures/pixels/waterVision"), new Vector2f(0, 0), new Vector2f(1, 1));
        guiTextures.add(new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f)));

        runGameLoop();
        cleanUp();
    }

    private void setTextures() {
        backgroundTexture = new TerrainTexture(loader.loadTexture("textures/pixels/dirt"));
        rTexture = new TerrainTexture(loader.loadTexture("textures/pixels/grass_top"));
        gTexture = new TerrainTexture(loader.loadTexture("textures/pixels/grass_path_top"));
        bTexture = new TerrainTexture(loader.loadTexture("textures/pixels/cobblestone"));
        blendMap = new TerrainTexture(loader.loadTexture("textures/blendMap1"));

        texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        tree1Texture = new TexturedModel(OBJLoader.loadObjModel("models/pine", loader), new ModelTexture(loader.loadTexture("textures/pixels/pine")));
        tree2Texture = new TexturedModel(OBJLoader.loadObjModel("models/lowPolyTree", loader), new ModelTexture(loader.loadTexture("textures/pixels/lowPolyTree")));
        fernTexture = new TexturedModel(OBJLoader.loadObjModel("models/fern", loader), new ModelTexture(loader.loadTexture("textures/pixels/fern")));
        lampTexture = new TexturedModel(OBJLoader.loadObjModel("models/lamp", loader), new ModelTexture(loader.loadTexture("textures/pixels/lamp")));
        playerTexture = new TexturedModel(OBJLoader.loadObjModel("models/person", loader), new ModelTexture(loader.loadTexture("textures/pixels/playerTexture")));
        boulderTexture = new TexturedModel(NormalMappedObjLoader.loadOBJ("models/boulder", loader), new ModelTexture(loader.loadTexture("textures/boulder")));

        tree1Texture.getTexture().setHasTransparency(true);
        fernTexture.getTexture().setHasTransparency(true);
        fernTexture.getTexture().setUseFakeLightning(true);
        fernTexture.getTexture().setNumberOfRows(2);
        lampTexture.getTexture().setUseFakeLightning(true);
        boulderTexture.getTexture().setNormalMap(loader.loadTexture("textures/boulderNormal"));
        boulderTexture.getTexture().setShineDamper(10);
        boulderTexture.getTexture().setReflectivity(0.5f);
    }

    private void setEntities() {
        for (int i = 0; i < 256; i++) {
            float x = random.nextFloat() * -800;
            float z = random.nextFloat() * -800;
            float y = terrains.get(0).getHeightOfTerrain(x, z);
            if (y > -4.0)
                entities.add(new Entity(fernTexture, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f));

            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            if (y > -4.0)
                entities.add(new Entity(tree2Texture, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f));

            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            if (y > -4.0)
                entities.add(new Entity(tree1Texture, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));

            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            normalEntities.add(new Entity(boulderTexture, new Vector3f(x, y, z), random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 0.4f + 0.2f));
        }
    }

    private void setWaters() {
        waterFrameBuffers = new WaterFrameBuffers();
        waterShader = new WaterShader();
        waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), waterFrameBuffers);
        waters.add(new WaterTile(-400, -400, -4));
    }

    private void setParticles() {
        particleSystem = new ParticleSystem(new ParticleTexture(loader.loadTexture("particles/particleAtlas"), 4, false), 50, 25, 0.3f, 4, 1);
        particleSystem.randomizeRotation();
        particleSystem.setDirection(new Vector3f(0, 1, 0), 0.1f);
        particleSystem.setLifeError(0.1f);
        particleSystem.setSpeedError(0.4f);
        particleSystem.setScaleError(0.8f);
    }

    private void runGameLoop() {
        while (!Display.isCloseRequested()) {
            DisplayManager.delta = DisplayManager.getDelta();
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) System.exit(0);

            update();

            DisplayManager.updateFPS();
            if (DisplayManager.canUpdate) System.out.println("FPS: " + DisplayManager.getFps());

            render();

            DisplayManager.updateDisplay();
        }
    }

    private void update() {
        renderer.tick();
        Input.tick();

        sun.update();
        player.move(terrains.get(0));
        camera.tick();
        picker.update();

        Vector3f terrainPoint = picker.getCurrentTerrainPoint();
        if (terrainPoint != null && lamps.size() <= 3 && Input.isMousePressed(2)) {
            Input.setMousePressed(false);
            lamps.add(new Lamp(lights, lampTexture, new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z), 0, 0, 0, 0.5f));
            if (lamps.size() > 3) {
                lamps.remove(0);
                lights.remove(1);
            }
        }
    }

    private void render() {
        renderer.renderShadowMap(entities, lights.get(0));

        GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

        waterFrameBuffers.bindReflectionFrameBuffer();
        float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
        camera.getPosition().y -= distance;
        camera.invertPitch();
        renderer.renderScene(entities, normalEntities, lamps, lights, terrains, camera, player, new Vector4f(0, 1, 0, -waters.get(0).getHeight()));
        camera.getPosition().y += distance;
        camera.invertPitch();

        waterFrameBuffers.bindRefractionFrameBuffer();
        renderer.renderScene(entities, normalEntities, lamps, lights, terrains, camera, player, new Vector4f(0, -1, 0, waters.get(0).getHeight()));

        waterFrameBuffers.unbindCurrentFrameBuffer();
        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
        renderer.renderScene(entities, normalEntities, lamps, lights, terrains, camera, player, new Vector4f(0, -1, 0, 1000000));
        waterRenderer.render(waters, camera, lights.get(0));

        if (camera.getPosition().y <= waters.get(0).getHeight()) guiRenderer.renderWater(waterVision);
        if (player.getPosition().y + 1.8f <= waters.get(0).getHeight()) {
            Player.underWater = true;
            Player.RUN_SPEED = 7.5f;
            Player.JUMP_POWER = 2.75f;
            Player.GRAVITY = -5;
        } else {
            Player.underWater = false;
            Player.RUN_SPEED = 15;
            Player.JUMP_POWER = 15;
            Player.GRAVITY = Player.REAL_GRAVITY;
        }

        ParticleMaster.renderParticles(camera);
        guiRenderer.render(guiTextures);
        TextMaster.render();
    }

    private void cleanUp() {
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        waterFrameBuffers.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

    public static void main(String[] args) {
        new MainGameLoop().init();
    }
}
