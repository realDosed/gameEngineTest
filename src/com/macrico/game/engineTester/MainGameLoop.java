package com.macrico.game.engineTester;

import com.macrico.game.entities.*;
import com.macrico.game.fontRendering.TextMaster;
import com.macrico.game.guis.GuiRenderer;
import com.macrico.game.guis.GuiTexture;
import com.macrico.game.models.TexturedModel;
import com.macrico.game.normalMappingObjConverter.NormalMappedObjLoader;
import com.macrico.game.objConverter.OBJFileLoader;
import com.macrico.game.particles.ParticleMaster;
import com.macrico.game.particles.ParticleSystem;
import com.macrico.game.particles.ParticleTexture;
import com.macrico.game.postProcessing.Fbo;
import com.macrico.game.postProcessing.PostProcessing;
import com.macrico.game.renderEngine.DisplayManager;
import com.macrico.game.renderEngine.Loader;
import com.macrico.game.renderEngine.MasterRenderer;
import com.macrico.game.terrains.Terrain;
import com.macrico.game.textures.ModelTexture;
import com.macrico.game.textures.TerrainTexture;
import com.macrico.game.textures.TerrainTexturePack;
import com.macrico.game.toolbox.Input;
import com.macrico.game.toolbox.MousePicker;
import com.macrico.game.waterTesting.WaterRendererTest;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

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

    private TerrainTexture blendMap;
    private TerrainTexturePack texturePack;
    private TexturedModel tree1Texture, tree2Texture, fernTexture, lampTexture, playerTexture, boulderTexture;

    private Player player;
    private Camera camera;
    private Sun sun;
    private MousePicker picker;

    private MasterRenderer renderer;
    private GuiRenderer guiRenderer;

    private com.macrico.game.waterTesting.WaterFrameBuffers frameBuffers;
    private com.macrico.game.waterTesting.WaterTile waterTile;
    private WaterRendererTest waterRendererTest;

    private Fbo multisampleFbo;
    private Fbo outputFbo;

    private void init() {
        DisplayManager.createDisplay();
        loader = new Loader();

        setTextures();
        terrains = new ArrayList<>();
        terrains.add(new Terrain(-1, -1, loader, texturePack, blendMap));

        entities = new ArrayList<>();
        normalEntities = new ArrayList<>();
        lamps = new ArrayList<>();
        lights = new ArrayList<>();
        guiTextures = new ArrayList<>();

        float x = random.nextFloat() * -800;
        float z = random.nextFloat() * -800;
        player = new Player(playerTexture, new Vector3f(x, 0, z), 0, random.nextFloat() * 360, 0, 0.2f);
        entities.add(player);

        camera = new Camera(player, terrains.get(0));

        renderer = new MasterRenderer(loader, camera);
        guiRenderer = new GuiRenderer(loader);
        picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains.get(0));
        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        TextMaster.init(loader);

        sun = new Sun(new Vector3f(10000, 15000, -10000));
        lights.add(sun);

        setWaters();
        setParticles();
        setEntities();

        for (int i = 0; i < 5; i++) {
            guiTextures.add(new GuiTexture(loader.loadTexture("textures/pixels/heart"), new Vector2f(-0.93f + (0.065f * i), 0.90f), new Vector2f(0.03f, 0.05f)));
        }

        multisampleFbo = new Fbo(Display.getWidth(), Display.getHeight());
        outputFbo = new Fbo(Display.getWidth(), Display.getHeight(), Fbo.DEPTH_TEXTURE);
        PostProcessing.init(loader);

        runGameLoop();
        cleanUp();
    }

    private void setTextures() {
        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/pixels/dirt"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("textures/pixels/grass_top"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("textures/pixels/grass_path_top"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("textures/pixels/cobblestone"));
        blendMap = new TerrainTexture(loader.loadTexture("textures/blendMap1"));

        texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        tree1Texture = new TexturedModel(OBJFileLoader.loadObjModel("models/pine", loader), new ModelTexture(loader.loadTexture("textures/pixels/pine")));
        tree2Texture = new TexturedModel(OBJFileLoader.loadObjModel("models/lowPolyTree", loader), new ModelTexture(loader.loadTexture("textures/pixels/lowPolyTree")));
        fernTexture = new TexturedModel(OBJFileLoader.loadObjModel("models/fern", loader), new ModelTexture(loader.loadTexture("textures/pixels/fern")));
        lampTexture = new TexturedModel(OBJFileLoader.loadObjModel("models/lamp", loader), new ModelTexture(loader.loadTexture("textures/pixels/lamp")));
        playerTexture = new TexturedModel(OBJFileLoader.loadObjModel("models/person", loader), new ModelTexture(loader.loadTexture("textures/pixels/playerTexture")));
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
            if (y > waterTile.getHeight())
                entities.add(new Entity(fernTexture, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f));

            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            if (y > waterTile.getHeight())
                entities.add(new Entity(tree2Texture, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f));

            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            if (y > waterTile.getHeight())
                entities.add(new Entity(tree1Texture, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));

            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            normalEntities.add(new Entity(boulderTexture, new Vector3f(x, y, z), random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 0.4f + 0.2f));
        }
    }

    private void setWaters() {
        frameBuffers = new com.macrico.game.waterTesting.WaterFrameBuffers();
        waterTile = new com.macrico.game.waterTesting.WaterTile(-1, -1, -3);
        waterRendererTest = new WaterRendererTest(loader, renderer.getProjectionMatrix(), frameBuffers);
    }

    private void setParticles() {
        ParticleSystem particleSystem = new ParticleSystem(new ParticleTexture(loader.loadTexture("particles/particleAtlas"), 4, false), 50, 25, 0.3f, 4, 1);
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
        frameBuffers.bindReflectionFrameBuffer();
        float distance = 2 * (camera.getPosition().y - waterTile.getHeight());
        camera.getPosition().y -= distance;
        camera.invertPitch();
        renderer.renderScene(entities, normalEntities, lamps, lights, terrains, camera, new Vector4f(0, 1, 0, -waterTile.getHeight()));
        camera.getPosition().y += distance;
        camera.invertPitch();

        frameBuffers.bindRefractionFrameBuffer();
        renderer.renderScene(entities, normalEntities, lamps, lights, terrains, camera, new Vector4f(0, -1, 0, waterTile.getHeight() + 1));

        frameBuffers.unbindCurrentFrameBuffer();
        GL11.glDisable(GL30.GL_CLIP_DISTANCE0);

        multisampleFbo.bindFrameBuffer();
        renderer.renderScene(entities, normalEntities, lamps, lights, terrains, camera, new Vector4f(0, 0, 0, 0));
        waterRendererTest.render(waterTile, camera, lights.get(0));
        ParticleMaster.renderParticles(camera);
        multisampleFbo.unbindFrameBuffer();
        multisampleFbo.resolveToFbo(outputFbo);

        PostProcessing.doPostProcessing(outputFbo.getColourTexture(), camera, waterTile.getHeight());

        if (player.getPosition().y + 1.5f <= waterTile.getHeight()) {
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

        guiRenderer.render(guiTextures);
        TextMaster.render();
    }

    private void cleanUp() {
        PostProcessing.cleanUp();
        outputFbo.cleanUp();
        multisampleFbo.cleanUp();
        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        frameBuffers.cleanUp();
        waterRendererTest.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }

    public static void main(String[] args) {
        new MainGameLoop().init();
    }
}
