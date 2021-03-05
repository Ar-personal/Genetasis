package engine.graphics;


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

        shader.createUniform("projectionMatrix");
        shader.createUniform("worldMatrix");

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    public void render(Window window, GameItem[] gameItems) {
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shader.bind();

        // Update projection Matrix
        Matrix4f projectionMatrix = transformation.getProjectionMatrix(FOV, window.getWidth(), window.getHeight(), Z_NEAR, Z_FAR);
        shader.setUniform("projectionMatrix", projectionMatrix);

        // Render each gameItem
        for(GameItem gameItem : gameItems) {
            // Set world matrix for this item
            Matrix4f worldMatrix =
                    transformation.getWorldMatrix(
                            gameItem.getPosition(),
                            gameItem.getRotation(),
                            gameItem.getScale());
            shader.setUniform("worldMatrix", worldMatrix);
            // Render the mes for this game item
            gameItem.getMesh().render();
        }

        shader.unbind();
    }

    public void cleanup() {
        if (shader != null) {
            shader.cleanup();
        }
    }
}
