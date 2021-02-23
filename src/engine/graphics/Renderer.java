package engine.graphics;

import engine.Window;
import engine.objects.Camera;
import engine.objects.GameObject;
import engine.objects.PointLight;
import maths.Matrix4f;
import maths.Transformation;
import maths.Vector3f;
import maths.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

public class Renderer {
    private Shader shader;
    private Window window;
    private final Transformation transformation;
    private float specularPower;

    public Renderer(Window window, Shader shader) {
        this.shader = shader;
        this.window = window;
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void renderMesh(GameObject object, Camera camera, Vector3f ambientLight, PointLight pointLight) throws Exception {
        GL30.glBindVertexArray(object.getMesh().getVAO());
        GL30.glEnableVertexAttribArray(0);
        GL30.glEnableVertexAttribArray(1);
        GL30.glEnableVertexAttribArray(2);
        GL30.glEnableVertexAttribArray(3);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, object.getMesh().getIBO());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL13.glBindTexture(GL11.GL_TEXTURE_2D, object.getMesh().getMaterial().getTextureID());
        shader.bind();
        shader.setUniform("model", Matrix4f.transform(object.getPosition(), object.getRotation(), object.getScale()));
        shader.setUniform("view", Matrix4f.view(camera.getPosition(), camera.getRotation()));
        shader.setUniform("projection", window.getProjectionMatrix());

        Matrix4f viewMatrix = transformation.getViewMatrix(camera);

        shader.createUniform("texture_sampler");
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createUniform("ambientLight");

        // Update Light Uniforms
        shader.setUniform("ambientLight", ambientLight);
        shader.setUniform("specularPower", specularPower);
        // Get a copy of the light object and transform its position to view coordinates
        PointLight currPointLight = new PointLight(pointLight);
        Vector3f lightPos = currPointLight.getPosition();
        Vector4f aux = new Vector4f(lightPos, 1);
        aux.multiply(aux, viewMatrix);
        lightPos.setX(aux.x);
        lightPos.setY(aux.y);
        lightPos.setZ(aux.z);
        shader.setUniform("pointLight", currPointLight);

        shader.createPointLightUniform("pointLight");

        GL11.glDrawElements(GL11.GL_TRIANGLES, object.getMesh().getIndices().length, GL11.GL_UNSIGNED_INT, 0);
        shader.unbind();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL30.glDisableVertexAttribArray(0);
        GL30.glDisableVertexAttribArray(1);
        GL30.glDisableVertexAttribArray(2);
        GL30.glDisableVertexAttribArray(3);
        GL30.glBindVertexArray(0);
    }
}
