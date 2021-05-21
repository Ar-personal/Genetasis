package main;

import engine.charts.BarChart;
import engine.charts.LineChart;
import engine.entities.*;
import engine.graphics.*;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.lights.SpotLight;
import engine.terrain.Camera;
import engine.terrain.Terrain;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class Game implements IGameLogic {
    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;
    private static final float CAMERA_POS_STEP = 0.05f;
    private final Renderer renderer;

    private final Camera camera;
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

    private LineChart lineChart = new LineChart("Entity Count", "number over time");
    private LineChart awarenessChart = new LineChart("Avg Awareness", "Awareness over time");
    private LineChart maxHungerChart = new LineChart("Avg Maximum Hunger", "Maximum Hunger over time");
    private LineChart maxEnergyChart = new LineChart("Avg Maximum Energy", "Maximum Energy over time");
    private LineChart maxGenerationChart = new LineChart("Generations", "Highest Generation over time");

    private List<LineChart> lineCharts = new ArrayList<>();

    private BarChart genders = new BarChart("Species Gender Quantities", "Categorical Species Genders");

    private List<BarChart> barCharts = new ArrayList<>();

    private Tree tree1, house;
    private Bush bush1, bush2, bush3, bush4, bush5, bush6;
    private Grass grass;

    private long  startTime, simStartTime;
    private long elapsedSimTime, elapsedSimSeconds, elapsedTime, elapsedSeconds;
    private int minutes = 0;
    private int waitAmt;

    public Game(){
        renderer = new Renderer();
        camera = new Camera();
        camera.setPosition(0, 0.5f, 0);
        cameraInc = new Vector3f(0.0f, 0.0f, 0.0f);
        lightAngle = 90;
        angleInc = 0;
    }

    @Override
    public void init(Window window) throws Exception {
        renderer.init(window);
        selectDetector = new MouseBoxSelectionDetector();

        if(window.getOpts().unlockFrameRate){
            waitAmt = 1;
        }else{
            waitAmt = 60;
        }


        //add all graphs to lists
        lineCharts.add(lineChart);
        lineCharts.add(awarenessChart);
        lineCharts.add(maxEnergyChart);
        lineCharts.add(maxGenerationChart);
        lineCharts.add(maxHungerChart);

        barCharts.add(genders);

        for(LineChart c  : lineCharts){
            c.pack();
            c.setVisible(true);
        }

        for(BarChart b  : barCharts){
            b.pack();
            b.setVisible(true);
        }


        startTime = System.currentTimeMillis();
        simStartTime = System.currentTimeMillis();
        elapsedSimSeconds = waitAmt;

        //create and load the terrain
        float terrainScale = 10;
        int terrainSize = 1;
        float minY = 0f;
        float maxY = 1f;
        int textInc = 40;
        terrain = new Terrain(terrainSize, terrainScale, minY, maxY, textInc);
        scene = new Scene(this, terrain, window);
        StaticGameItem[] items = terrain.getGameItems();

        for(int i = 0; i < items.length; i++) {
            scene.addStaticGameItem(items[i]);
            items[i].setPosition(-10, 0 ,-10);

        }
//
        for(int i = 0; i < 40; i++) {
            scene.addDeer(null, false, null, null);
        }
//
        for(int i = 0; i < 25; i++) {
            scene.addTiger(null, false, null, null);
        }
//

        for(int i = 0; i < 300; i++) {
            scene.addGrass();
        }

        // Setup  GameItems
        float reflectance = 1f;

//        Mesh[] houseMesh = StaticMeshesLoader.load("C:\\Users\\Alex\\Dropbox\\Game Design\\Genetasis\\resources\\models\\house\\farmhouse_obj.obj", "C:\\Users\\Alex\\Dropbox\\Game Design\\Genetasis\\resources\\models\\house");
//        Tree house = new Tree(houseMesh, randomSpawn());
//        house.setScale(0.1f);
//        scene.addStaticGameItem(house)



//        Mesh[] treeMesh = StaticMeshesLoader.load("C:\\Users\\Alex\\Dropbox\\Game Design\\Genetasis\\resources\\models\\tree1\\lowpolytree.obj", "C:\\Users\\Alex\\Dropbox\\Game Design\\Genetasis\\resources\\models\\tree1");
//        Tree tree1 = new Tree(treeMesh, randomSpawn());
//        tree1.setScale(1f);
//        scene.addStaticGameItem(tree1);

//        Mesh cubeMesh = new OBJLoader().loadMesh("/models/cube.obj", null, true);
//        Mesh bush1Mesh = new OBJLoader().loadMesh("/models/Bush1.obj", new Vector3f(144, 156, 30), false);
//        Mesh bush2Mesh = new OBJLoader().loadMesh("/models/BerryBush1.obj", new Vector3f(40, 15, 30), false);

//        Mesh treeMesh = new OBJLoader().loadMesh("/models/arbre.obj", new Vector3f(40, 255, 40), false);
//        Mesh bush3Mesh = OBJLoader.loadMesh("/models/Bush3.obj", new Vector3f(144, 16, 80));
//        Mesh bush4Mesh = OBJLoader.loadMesh("/models/BerryBush1.obj", new Vector3f(90, 196, 30));
//        Mesh bush5Mesh = OBJLoader.loadMesh("/models/BerryBush2.obj", new Vector3f(18, 209, 30));
//        Mesh bush6Mesh = OBJLoader.loadMesh("/models/BerryBush3.obj", new Vector3f(200, 156, 255));


//        cubeMesh.setMaterial(cubeMaterial);
//        bush1Mesh.setMaterial(cubeMaterial);
//        cubeStaticGameItem = new BoundingBox(cubeMesh, randomSpawn());
//        cubeStaticGameItem.setScale(0.1f);
//
//        bush1 = new Bush(bush1Mesh, randomSpawn());
//        bush1.setScale(0.2f);
//
//        bush2 = new Bush(bush2Mesh, randomSpawn());
//        bush2.setScale(0.2f);
//
////

////
//
//        for(int i = 0; i < 20; i++) {
//            tree1 = new Tree(treeMesh, randomSpawn());
//            tree1.setScale(0.1f);
//            scene.addStaticGameItem(tree1);
////            scene.addBoundingBox(tree1.getBoundingBox());
//        }
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
        sceneLight.setAmbientLight(new Vector3f(0.3f, 0.3f, 0.3f));
//        sceneLight.setSkyBoxLight(new Vector3f(1.0f, 1.0f, 1.0f));

        // Directional Light
        float lightIntensity = 1.0f;
        Vector3f lightDirection = new Vector3f(0, 1, 1);
        DirectionalLight directionalLight = new DirectionalLight(new Vector3f(1, 1, 1), lightDirection, lightIntensity);
        sceneLight.setDirectionalLight(directionalLight);
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
    public void update(float interval, MouseInput mouseInput, Window window) throws Exception {
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


        lightAngle += angleInc;
        if ( lightAngle < 0 ) {
            lightAngle = 0;
        } else if (lightAngle > 180 ) {
            lightAngle = 180;
        }
        float zValue = (float)Math.cos(Math.toRadians(lightAngle));
        float yValue = (float)Math.sin(Math.toRadians(lightAngle));
        Vector3f lightDirection = scene.getSceneLight().getDirectionalLight().getDirection();
        lightDirection.x = 0;
        lightDirection.y = yValue;
        lightDirection.z = zValue;
        lightDirection.normalize();

        camera.updateViewMatrix();
        if (mouseInput.isLeftButtonPressed()) {
            this.selectDetector.selectGameItem(scene.getGameItems(), window, mouseInput.getCurrentPos(), camera);
        }

        scene.update();


        //update graphs every x seconds
        if(elapsedSimSeconds >= waitAmt){

            if(!window.getOpts().unlockFrameRate) {
                lineChart.addToDataset(scene.getDeers().size(), scene.getTigerList().size(), minutes, "Number Of Animals");
                awarenessChart.addToDataset(scene.averageDeerAwareness(), scene.averageTigerAwareness(), minutes, "Awareness");
                maxHungerChart.addToDataset(scene.averageDeerMaxHunger(), scene.averageTigerMaxHunger(), minutes, "MaxHunger");
                maxEnergyChart.addToDataset(scene.averageDeerMaxEnergy(), scene.averageTigerMaxEnergy(), minutes, "MaxEnergy");
                maxGenerationChart.addToDataset(scene.maxDeerGeneration(), scene.maxTigerGeneration(), minutes, "Max Generation");

                scene.countGenders();
                genders.resetDataset(scene.getDeerMales(), scene.getDeerFemales(), scene.getTigerMales(), scene.getTigerFemales());
            }else{
                lineChart.addToDataset(scene.getDeers().size(), scene.getTigerList().size(), (int) elapsedSeconds, "Amount Of Animals");
                awarenessChart.addToDataset(scene.averageDeerAwareness(), scene.averageTigerAwareness(), (int) elapsedSeconds, "Awareness");
                maxHungerChart.addToDataset(scene.averageDeerMaxHunger(), scene.averageTigerMaxHunger(), (int) elapsedSeconds, "MaxHunger");
                maxEnergyChart.addToDataset(scene.averageDeerMaxEnergy(), scene.averageTigerMaxEnergy(), (int) elapsedSeconds, "MaxEnergy");
                maxGenerationChart.addToDataset(scene.maxDeerGeneration(), scene.maxTigerGeneration(), (int) elapsedSeconds, "Max Generation");
                genders.resetDataset(scene.getDeerMales(), scene.getDeerFemales(), scene.getTigerMales(), scene.getTigerFemales());
            }

            for(LineChart c  : lineCharts){
                c.repaint();
                c.setVisible(true);
            }

            for(BarChart b : barCharts){
//                b.repaint();
                b.setVisible(true);

            }

            simStartTime = System.currentTimeMillis();
            elapsedSimTime = 0;
            elapsedSimSeconds = 0;
            minutes++;
        }

        elapsedSimTime = System.currentTimeMillis() - simStartTime;
        elapsedSimSeconds = elapsedSimTime / 1000;
        elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;

        System.out.println(scene.getDeers().size());
    }

    public void render(Window window, MouseInput mouseInput) {
        renderer.render(window, camera, scene);
        scene.updateMeshMap();
        scene.getHud().render(window, mouseInput);
        if(scene.isEntitySelected()) {
            scene.getEntityHud().render();
        }
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        scene.cleanup();
    }

    public Scene getScene() {
        return scene;
    }

}


