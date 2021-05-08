package main;

import engine.entities.*;
import engine.graphics.*;
import engine.objects.Camera;
import engine.objects.Terrain;
import engine.utils.OBJLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements IGameLogic {
    private static final float MOUSE_SENSITIVITY = 0.2f;
    private int displxInc = 0;

    private int displyInc = 0;

    private int displzInc = 0;

    private int scaleInc = 0;

    private final Vector3f cameraInc;
    private static final float CAMERA_POS_STEP = 0.05f;
    private final Renderer renderer;

    private StaticGameItem[] staticGameItems;
    private final Camera camera;
    private Hud hud;
    private Terrain terrain;
//    private CameraBoxSelectionDetector selectDetector;
    private MouseBoxSelectionDetector selectDetector;


    private Vector3f ambientLight;

    private PointLight[] pointLightList;

    private SpotLight[] spotLightList;

    private DirectionalLight directionalLight;

    private SceneLight sceneLight;

    private Scene scene;

    private float lightAngle;

    private float spotAngle = 0;

    private float spotInc = 1, angleInc;

    private BoundingBox cubeStaticGameItem;

    private Bush bush1, bush2, bush3, bush4, bush5, bush6;
    private Grass grass;

    public Game() throws Exception {
        renderer = new Renderer();
        camera = new Camera();
        camera.setPosition(0, 0.5f, 0);
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = -90;
        angleInc = 0;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        selectDetector = new MouseBoxSelectionDetector();


        //create and load the terrain
        float terrainScale = 10;
        int terrainSize = 1;
        float minY = 0f;
        float maxY = 1f;
        int textInc = 40;
//        terrain = new Terrain(terrainSize, terrainScale, minY, maxY, "C:\\Users\\Alex\\Dropbox\\Game Design\\Genetasis\\resources\\textures\\heightmap.png", null, textInc);
        terrain = new Terrain(terrainSize, terrainScale, minY, maxY, textInc);
        scene = new Scene(this, terrain, window);
        StaticGameItem[] items = terrain.getGameItems();

        for(int i = 0; i < items.length; i++) {
            scene.addStaticGameItem(items[i]);
            items[i].setPosition(-10, 0 ,-10);

        }

        scene.addDeers(5);


        // Setup  GameItems
        float reflectance = 1f;

        Mesh cubeMesh = new OBJLoader().loadMesh("/models/cube.obj", null, true);
        Mesh bush1Mesh = new OBJLoader().loadMesh("/models/Bush1.obj", new Vector3f(144, 156, 30), false);
        Mesh bush2Mesh = new OBJLoader().loadMesh("/models/BerryBush1.obj", new Vector3f(40, 15, 30), false);
        Mesh grassMesh = new OBJLoader().loadMesh("/models/grass.obj", new Vector3f(50, 255, 50), false);
//        Mesh bush3Mesh = OBJLoader.loadMesh("/models/Bush3.obj", new Vector3f(144, 16, 80));
//        Mesh bush4Mesh = OBJLoader.loadMesh("/models/BerryBush1.obj", new Vector3f(90, 196, 30));
//        Mesh bush5Mesh = OBJLoader.loadMesh("/models/BerryBush2.obj", new Vector3f(18, 209, 30));
//        Mesh bush6Mesh = OBJLoader.loadMesh("/models/BerryBush3.obj", new Vector3f(200, 156, 255));

        Material cubeMaterial = new Material();
//        cubeMesh.setMaterial(cubeMaterial);
//        bush1Mesh.setMaterial(cubeMaterial);
        cubeStaticGameItem = new BoundingBox(cubeMesh, randomSpawn());
        cubeStaticGameItem.setScale(0.1f);

        bush1 = new Bush(bush1Mesh, randomSpawn());
        bush1.setScale(0.2f);

        bush2 = new Bush(bush2Mesh, randomSpawn());
        bush2.setScale(0.2f);


        for(int i = 0; i < 90; i++) {
            grass = new Grass(grassMesh, randomSpawn());
            grass.setScale(0.1f);
            scene.addStaticGameItem(grass);
            scene.addGrassItem(grass);
//            scene.addBoundingBox(grass.getBoundingBox());
        }
//        bush4 = new StaticGameItem(bush4Mesh);
//        bush4.setPosition(0.4f, 0.5f, 0);
//        bush4.setScale(0.5f);
//
//        bush5 = new StaticGameItem(bush5Mesh);
//        bush5.setPosition(0.5f, 0.5f, 0);
//        bush5.setScale(0.5f);

//        bush6 = new StaticGameItem(bush6Mesh);
//        bush6.setPosition(0.6f, 0.5f, 0);
//        bush6.setScale(0.5f);



;

//        scene.addStaticGameItem(bush4);
//        scene.addStaticGameItem(bush5);
//        scene.addStaticGameItem(bush6);


        // Setup Lights
        setupLights();

        camera.getPosition().z = 2;
        camera.getPosition().y = 1;
    }

    private void setupLights() {
        SceneLight sceneLight = new SceneLight();
        scene.setSceneLight(sceneLight);

        // Ambient Light
//        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
//        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        directionalLight.setShadowPosMult(5);
        directionalLight.setOrthoCords(-10.0f, 10.0f, -10.0f, 10.0f, -1.0f, 20.0f);
        sceneLight.setDirectionalLight(directionalLight);
    }

    public Vector3f randomSpawn(){
        Random r = new Random();
        float x = r.nextFloat() * 30f - 10f;
        float z = r.nextFloat() * 30f - 10f;
        float y = r.nextFloat();
        //10f for world offset
        Vector3f vec = new Vector3f(x, 1f , z);
        float pos = terrain.getHeight(vec);

        return new Vector3f(x, pos, z);
    }


    @Override
    public void input(Window window, MouseInput mouseInput) {
        float cameraspeed = 2f;
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -cameraspeed;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = cameraspeed;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -cameraspeed;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = cameraspeed;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -cameraspeed;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = cameraspeed;
        }

        if (window.isKeyPressed(GLFW_KEY_LEFT)) {
            angleInc -= 0.05f;
        } else if (window.isKeyPressed(GLFW_KEY_RIGHT)) {
            angleInc += 0.05f;
        } else {
            angleInc = 0;
        }
    }


    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {
        scene.update();

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed()) {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
        }

        // Update camera position
        Vector3f prevPos = new Vector3f(camera.getPosition());
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
        // Check if there has been a collision. If true, set the y position to
        // the maximum height


        float rotY = cubeStaticGameItem.getRotation().y;
        rotY += 0.5f;
        if ( rotY >= 360 ) {
            rotY -= 360;
        }
        cubeStaticGameItem.getRotation().y = rotY;

        lightAngle += angleInc;
        if ( lightAngle < 0 ) {
            lightAngle = 0;
        } else if (lightAngle > 180 ) {
            lightAngle = 180;
        }
        float zValue = (float)Math.cos(Math.toRadians(lightAngle));
        float yValue = (float)Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = this.scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();
        float lightAngle = (float)Math.toDegrees(Math.acos(lightDirection.z));


        camera.updateViewMatrix();
        if (mouseInput.isLeftButtonPressed()) {
            this.selectDetector.selectGameItem(scene.getGameItems(), window, mouseInput.getCurrentPos(), camera);
        }
    }

    public void render(Window window) {
        renderer.render(window, camera, scene);
        if(scene.isEntitySelected()) {
            scene.getEntityHud().render();
        }
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
//        scene.cleanup();
        if (hud != null) {
            hud.cleanup();
        }
    }

    public Scene getScene() {
        return scene;
    }

}


