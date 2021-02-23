package main;

import engine.Input;
import engine.Window;
import engine.graphics.*;
import engine.objects.PointLight;
import engine.utils.ModelLoader;
import maths.Vector3f;
import maths.Vector4f;
import org.lwjgl.glfw.GLFW;

import engine.objects.Camera;
import engine.objects.GameObject;

import static org.lwjgl.glfw.GLFW.glfwPollEvents;

public class Main implements Runnable {
    public Thread game;
    public Window window;
    public Renderer renderer;
    public Shader shader;
    public final int WIDTH = 1280, HEIGHT = 760;
    private Vector3f ambientLight;
    private PointLight pointLight;

//    public Mesh mesh = ModelLoader.loadModel("resources/models/dragon.obj", "/textures/beautiful.png");

    public GameObject[] objects = new GameObject[1];

//    public GameObject object = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), mesh);

    public Camera camera = new Camera(new Vector3f(0, 0, 1), new Vector3f(0, 0, 0));

    public Mesh meshCube = ModelLoader.loadModel("resources/models/cube.obj", "/textures/beautiful.png");
    public GameObject cubeObject = new GameObject(new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), meshCube);


    public void start() {
        game = new Thread(this, "game");
        game.start();
    }

    public void init() {
        window = new Window(WIDTH, HEIGHT, "Game");
        shader = new Shader("/shaders/mainVertex.glsl", "/shaders/mainFragment.glsl");
        renderer = new Renderer(window, shader);
        window.setBackgroundColor(1.0f, 0, 0);
        window.create();
//        mesh.create();
        shader.create();
        meshCube.create();

        ambientLight = new Vector3f(0.3f, 0.3f, 0.3f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        Vector3f lightPosition = new Vector3f(0, 0, 1);
        float lightIntensity = 1.0f;
        pointLight = new PointLight(lightColour, lightPosition, lightIntensity, new Vector3f(0, 0, 0));
        PointLight.Attenuation att = new PointLight.Attenuation(0.0f, 0.0f, 1.0f);
        pointLight.setAttenuation(att);


        objects[0] = cubeObject;
        for (int i = 0; i < objects.length; i++) {
            objects[i] = new GameObject(new Vector3f((float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25), (float) (Math.random() * 50 - 25)), new Vector3f(0, 0, 0), new Vector3f(1, 1, 1), meshCube);
        }
    }

    public void run() {
        init();
        while (!window.shouldClose() && !Input.isKeyDown(GLFW.GLFW_KEY_ESCAPE)) {
            update();
            try {
                render();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (Input.isKeyDown(GLFW.GLFW_KEY_F11)) window.setFullscreen(!window.isFullscreen());
            if (Input.isButtonDown(GLFW.GLFW_MOUSE_BUTTON_LEFT)) window.mouseState(true);
        }
        close();
    }

    private void update() {
        window.update();
        camera.update();
        pointLight.update();
    }

    private void render() throws Exception {
        for (int i = 0; i < objects.length; i++) {
            renderer.renderMesh(objects[i], camera, ambientLight, pointLight);
        }
        window.swapBuffers();
        glfwPollEvents();
    }

    private void close() {
        window.destroy();
//        mesh.destroy();
        meshCube.destroy();
        shader.destroy();
    }

    public static void main(String[] args) {
        new Main().start();
    }
}
