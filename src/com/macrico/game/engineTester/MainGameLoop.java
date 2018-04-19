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

    public static void main(String[] args) {
        DisplayManager.createDisplay();
        Random random = new Random();
        Loader loader = new Loader();

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/pixels/dirt"));
        TerrainTexture rTexture = new TerrainTexture(loader.loadTexture("textures/pixels/grass_top"));
        TerrainTexture gTexture = new TerrainTexture(loader.loadTexture("textures/pixels/grass_path_top"));
        TerrainTexture bTexture = new TerrainTexture(loader.loadTexture("textures/pixels/cobblestone"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("textures/blendMap1"));

        TerrainTexturePack texturePack = new TerrainTexturePack(backgroundTexture, rTexture, gTexture, bTexture);

        ModelData tree1_data = OBJFileLoader.loadOBJ("models/pine");
        ModelData tree2_data = OBJFileLoader.loadOBJ("models/lowPolyTree");
        ModelData fern_data = OBJFileLoader.loadOBJ("models/fern");
        ModelData lamp_data = OBJFileLoader.loadOBJ("models/lamp");
        ModelData player_data = OBJFileLoader.loadOBJ("models/person");

        ModelTexture treeTexture_1 = new ModelTexture(loader.loadTexture("textures/pixels/pine"));
        ModelTexture treeTexture_2 = new ModelTexture(loader.loadTexture("textures/pixels/lowPolyTree"));
        ModelTexture grassTexture = new ModelTexture(loader.loadTexture("textures/pixels/tallgrass"));
        ModelTexture fernTexture = new ModelTexture(loader.loadTexture("textures/pixels/fern"));
        ModelTexture flowerTexture = new ModelTexture(loader.loadTexture("textures/pixels/flower_rose"));
        ModelTexture lampTexture = new ModelTexture(loader.loadTexture("textures/pixels/lamp"));
        ModelTexture playerTexture = new ModelTexture(loader.loadTexture("textures/pixels/playerTexture"));

        TexturedModel tree_1 = new TexturedModel(loader.loadToVAO(tree1_data.getVertices(), tree1_data.getTextureCoords(), tree1_data.getNormals(), tree1_data.getIndices()), treeTexture_1);
        TexturedModel tree_2 = new TexturedModel(loader.loadToVAO(tree2_data.getVertices(), tree2_data.getTextureCoords(), tree2_data.getNormals(), tree2_data.getIndices()), treeTexture_2);
        TexturedModel fern = new TexturedModel(loader.loadToVAO(fern_data.getVertices(), fern_data.getTextureCoords(), fern_data.getNormals(), fern_data.getIndices()), fernTexture);
        TexturedModel lamp = new TexturedModel(loader.loadToVAO(lamp_data.getVertices(), lamp_data.getTextureCoords(), lamp_data.getNormals(), lamp_data.getIndices()), lampTexture);
        TexturedModel player = new TexturedModel(loader.loadToVAO(player_data.getVertices(), player_data.getTextureCoords(), player_data.getNormals(), player_data.getIndices()), playerTexture);
        TexturedModel boulderModel = new TexturedModel(NormalMappedObjLoader.loadOBJ("models/boulder", loader), new ModelTexture(loader.loadTexture("textures/boulder")));

        treeTexture_1.setHasTransparency(true);
        grassTexture.setHasTransparency(true);
        grassTexture.setUseFakeLightning(true);
        flowerTexture.setHasTransparency(true);
        flowerTexture.setUseFakeLightning(true);
        fernTexture.setHasTransparency(true);
        fernTexture.setUseFakeLightning(true);
        fernTexture.setNumberOfRows(2);
        lampTexture.setUseFakeLightning(true);
        boulderModel.getTexture().setNormalMap(loader.loadTexture("textures/boulderNormal"));
        boulderModel.getTexture().setShineDamper(10);
        boulderModel.getTexture().setReflectivity(0.5f);

        List<Terrain> terrains = new ArrayList<Terrain>();
        terrains.add(new Terrain(-1, -1, loader, texturePack, blendMap));

        Player playerEntity;
        float x = random.nextFloat() * -800;
        float z = random.nextFloat() * -800;
        float y = terrains.get(0).getHeightOfTerrain(x, z);
        if (y > -4.0) playerEntity = new Player(player, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.2f);
        else playerEntity = new Player(player, new Vector3f(-200, 0, -200), 0, random.nextFloat() * 360, 0, 0.2f);

        Camera camera = new Camera(playerEntity, terrains.get(0));
        MasterRenderer renderer = new MasterRenderer(loader, camera);
        ParticleMaster.init(loader, renderer.getProjectionMatrix());
        GuiRenderer guiRenderer = new GuiRenderer(loader);
        TextMaster.init(loader);

        List<Entity> entities = new ArrayList<Entity>();
        List<Entity> normalEntities = new ArrayList<Entity>();
        List<Lamp> lamps = new ArrayList<Lamp>();
        List<Light> lights = new ArrayList<Light>();

        Sun sun = new Sun(new Vector3f(10000, 15000, -10000));
        lights.add(sun);

//        FontType font = new FontType(loader.loadFontTexture("font/Candara/candara"), new File("res/font/Candara/candara.fnt"));
//        GUIText fpsText = new GUIText("", 1, font, new Vector2f(0, 0.01f), 1, true);
//        fpsText.setColor(1, 1, 1);

        for (int i = 0; i < 256; i++) {
            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            if (y > -4.0)
                entities.add(new Entity(fern, random.nextInt(4), new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f));

            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            if (y > -4.0)
                entities.add(new Entity(tree_2, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 0.5f));

            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            if (y > -4.0)
                entities.add(new Entity(tree_1, new Vector3f(x, y, z), 0, random.nextFloat() * 360, 0, 1));

            x = random.nextFloat() * -800;
            z = random.nextFloat() * -800;
            y = terrains.get(0).getHeightOfTerrain(x, z);
            normalEntities.add(new Entity(boulderModel, new Vector3f(x, y, z), random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 360, random.nextFloat() * 0.4f + 0.2f));
        }

        List<GuiTexture> guis = new ArrayList<GuiTexture>();
        GuiTexture vignette = new GuiTexture(loader.loadTexture("textures/pixels/vignetteTest"), new Vector2f(0, 0), new Vector2f(1, 1));
        GuiTexture waterVision = new GuiTexture(loader.loadTexture("textures/pixels/waterVision"), new Vector2f(0, 0), new Vector2f(1, 1));

        guis.add(vignette);
        for (int i = 0; i < 5; i++) {
            guis.add(new GuiTexture(loader.loadTexture("textures/pixels/heart"), new Vector2f(-0.93f + (0.065f * i), 0.90f), new Vector2f(0.03f, 0.05f)));
        }

        MousePicker picker = new MousePicker(camera, renderer.getProjectionMatrix(), terrains.get(0));

        WaterFrameBuffers fbos = new WaterFrameBuffers();
        WaterShader waterShader = new WaterShader();
        WaterRenderer waterRenderer = new WaterRenderer(loader, waterShader, renderer.getProjectionMatrix(), fbos);
        List<WaterTile> waters = new ArrayList<WaterTile>();
        waters.add(new WaterTile(-400, -400, -4));

        ParticleTexture particleTexture = new ParticleTexture(loader.loadTexture("particles/particleAtlas"), 4, false);

        ParticleSystem system = new ParticleSystem(particleTexture, 50, 25, 0.3f, 4, 1);
        system.randomizeRotation();
        system.setDirection(new Vector3f(0, 1, 0), 0.1f);
        system.setLifeError(0.1f);
        system.setSpeedError(0.4f);
        system.setScaleError(0.8f);

        GuiTexture shadowMap = new GuiTexture(renderer.getShadowMapTexture(), new Vector2f(0.5f, 0.5f), new Vector2f(0.5f, 0.5f));
        guis.add(shadowMap);

        while (!Display.isCloseRequested()) {
            DisplayManager.delta = DisplayManager.getDelta();
            if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) System.exit(0);

            // ***** GAME UPDATES *****

            renderer.tick();
            Input.tick();

            sun.update();
            playerEntity.move(terrains.get(0));
            camera.tick();
            picker.update();

//            system.generateParticles(playerEntity.getPosition());
//            ParticleMaster.update(camera);

            Vector3f terrainPoint = picker.getCurrentTerrainPoint();
            if (terrainPoint != null && lamps.size() <= 3 && Input.isMousePressed(2)) {
                Input.setMousePressed(false);
                lamps.add(new Lamp(lights, lamp, new Vector3f(terrainPoint.x, terrainPoint.y, terrainPoint.z), 0, 0, 0, 0.5f));
                if (lamps.size() > 3) {
                    lamps.remove(0);
                    lights.remove(1);
                }
            }

            // *****

            DisplayManager.updateFPS();
            if (DisplayManager.canUpdate) {
                System.out.println("FPS: " + DisplayManager.getFps());
//                TextMaster.remove(fpsText);
//                fpsText.setTextString("FPS: " + DisplayManager.getFps());
//                TextMaster.loadText(fpsText);
            }

            // ***** GAME RENDER *****

            renderer.renderShadowMap(entities, lights.get(0));

            GL11.glEnable(GL30.GL_CLIP_DISTANCE0);

            fbos.bindReflectionFrameBuffer();
            float distance = 2 * (camera.getPosition().y - waters.get(0).getHeight());
            camera.getPosition().y -= distance;
            camera.invertPitch();
            renderer.renderScene(entities, normalEntities, lamps, lights, terrains, camera, playerEntity, new Vector4f(0, 1, 0, -waters.get(0).getHeight()));
            camera.getPosition().y += distance;
            camera.invertPitch();

            fbos.bindRefractionFrameBuffer();
            renderer.renderScene(entities, normalEntities, lamps, lights, terrains, camera, playerEntity, new Vector4f(0, -1, 0, waters.get(0).getHeight()));

            fbos.unbindCurrentFrameBuffer();
            GL11.glDisable(GL30.GL_CLIP_DISTANCE0);
            renderer.renderScene(entities, normalEntities, lamps, lights, terrains, camera, playerEntity, new Vector4f(0, -1, 0, 1000000));
            waterRenderer.render(waters, camera, lights.get(0));

            if (camera.getPosition().y <= waters.get(0).getHeight()) {
                guiRenderer.renderWater(waterVision);
            }
            if (playerEntity.getPosition().y + 1.8 <= waters.get(0).getHeight()) {
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

            guiRenderer.render(guis);
            TextMaster.render();

            // *****

            DisplayManager.updateDisplay();
        }

        ParticleMaster.cleanUp();
        TextMaster.cleanUp();
        fbos.cleanUp();
        waterShader.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        DisplayManager.closeDisplay();
    }
}
