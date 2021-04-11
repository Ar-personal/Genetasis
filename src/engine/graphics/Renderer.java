package engine.graphics;


import engine.objects.Camera;
import engine.utils.Utils;
import main.GameItem;
import main.Transformation;
import org.joml.Matrix4f;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private org.joml.Matrix4f projectionMatrix;

    private Shader shader;
    private Transformation transformation;
    private int vboId;

    private int vaoId;

    public Renderer() {
        transformation = new Transformation();
    }

    public void init(Window window) throws Exception {
        shader = new Shader();

        shader.createVertexShader(Utils.loadResource("/shaders/mainVertex.glsl"));
        shader.createFragmentShader(Utils.loadResource("/shaders/mainFragment.glsl"));
        shader.link();

        float aspectRatio = (float) window.getWidth() / window.getHeight();
        projectionMatrix = new Matrix4f().perspective(Renderer.FOV, aspectRatio, Renderer.Z_NEAR, Renderer.Z_FAR);
        shader.createUniform("projectionMatrix");
        shader.createUniform("modelViewMatrix");
        shader.createUniform("texture_sampler");

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, GameItem[] gameItems, Camera camera) {
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shader.bind();


        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shader.setUniform("projectionMatrix", projectionMatrix);

        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shader.setUniform("texture_sampler", 0);
        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            Mesh mesh = gameItem.getMesh();
            // Set model view matrix for this item
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shader.setUniform("modelViewMatrix", modelViewMatrix);

            mesh.render();
        }

        shader.unbind();
    }

    public void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }
    }
}
