package engine.graphics;


import engine.entities.GameItem;
import engine.lights.DirectionalLight;
import engine.lights.PointLight;
import engine.lights.SceneLight;
import engine.lights.SpotLight;
import engine.terrain.Camera;
import engine.terrain.ShadowMap;
import engine.utils.Utils;
import main.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Renderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private static final int MAX_POINT_LIGHTS = 5;

    private static final int MAX_SPOT_LIGHTS = 5;

    private final Transformation transformation;
    private ShadowMap shadowMap;

    private Shader sceneShaderProgram;
    private Shader skyBoxShaderProgram;
    private Shader depthShaderProgram;

    private final float specularPower;

    public Renderer() {
        transformation = new Transformation();
        specularPower = 10f;
    }

    public void init(Window window) throws Exception {
        shadowMap = new ShadowMap();

        setupSceneShader();
        setupSkyBoxShader();
        setupDepthShader();
    }

    private void setupSceneShader() throws Exception {
        // Create shader
        sceneShaderProgram = new Shader();
        sceneShaderProgram.createVertexShader(Utils.loadResource("/shaders/mainVertex.glsl"));
        sceneShaderProgram.createFragmentShader(Utils.loadResource("/shaders/mainFragment.glsl"));
        sceneShaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        sceneShaderProgram.createUniform("projectionMatrix");
        sceneShaderProgram.createUniform("modelViewMatrix");
        sceneShaderProgram.createUniform("texture_sampler");
        sceneShaderProgram.createUniform("normalMap");
        // Create uniform for material
        sceneShaderProgram.createMaterialUniform("material");
        // Create lighting related uniforms
        sceneShaderProgram.createUniform("specularPower");
        sceneShaderProgram.createUniform("ambientLight");
        sceneShaderProgram.createPointLightListUniform("pointLights", MAX_POINT_LIGHTS);
        sceneShaderProgram.createSpotLightListUniform("spotLights", MAX_SPOT_LIGHTS);
        sceneShaderProgram.createDirectionalLightUniform("directionalLight");
//        sceneShaderProgram.createFogUniform("fog");

        sceneShaderProgram.createUniform("shadowMap");
        sceneShaderProgram.createUniform("orthoProjectionMatrix");
        sceneShaderProgram.createUniform("modelLightViewMatrix");
        sceneShaderProgram.createUniform("selected");

        sceneShaderProgram.createUniform("numCols");
        sceneShaderProgram.createUniform("numRows");

    }

    private void setupSkyBoxShader() throws Exception {
        skyBoxShaderProgram = new Shader();
        skyBoxShaderProgram.createVertexShader(Utils.loadResource("/shaders/skybox_vertex.glsl"));
        skyBoxShaderProgram.createFragmentShader(Utils.loadResource("/shaders/skybox_fragment.glsl"));
        skyBoxShaderProgram.link();

        skyBoxShaderProgram.createUniform("projectionMatrix");
        skyBoxShaderProgram.createUniform("modelViewMatrix");
        skyBoxShaderProgram.createUniform("texture_sampler");
        skyBoxShaderProgram.createUniform("ambientLight");
    }

    private void setupDepthShader() throws Exception {
        depthShaderProgram = new Shader();
        depthShaderProgram.createVertexShader(Utils.loadResource("/shaders/depth_vertex.glsl"));
        depthShaderProgram.createFragmentShader(Utils.loadResource("/shaders/depth_fragment.glsl"));
        depthShaderProgram.link();

        depthShaderProgram.createUniform("orthoProjectionMatrix");
        depthShaderProgram.createUniform("modelLightViewMatrix");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void render(Window window, Camera camera,
                       Scene scene) {

        clear();

        renderDepthMap(window, camera, scene);

        glViewport(0, 0, window.getWidth(), window.getHeight());

//        window.updateProjectionMatrix();

        window.updateProjectionMatrix();
        transformation.updateViewMatrix(camera);

        renderScene(window, camera, scene);


//        renderSkyBox(window, camera, scene);
    }

    public void renderScene(Window window, Camera camera, Scene scene) {
        sceneShaderProgram.bind();

        Matrix4f projectionMatrix = window.getProjectionMatrix();
        sceneShaderProgram.setUniform("projectionMatrix", projectionMatrix);
        Matrix4f orthoProjMatrix = transformation.getOrthoProjectionMatrix();
        sceneShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);
        Matrix4f lightViewMatrix = transformation.getLightViewMatrix();

        Matrix4f viewMatrix = transformation.getViewMatrix();

        SceneLight sceneLight = scene.getSceneLight();
        renderLights(viewMatrix, sceneLight);

//        sceneShaderProgram.setUniform("fog", scene.getFog());
        sceneShaderProgram.setUniform("texture_sampler", 0);
        sceneShaderProgram.setUniform("normalMap", 1);
        sceneShaderProgram.setUniform("shadowMap", 2);


;

        // Render each mesh with the associated game Items
        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            if(mesh.getMaterial() != null) {
                sceneShaderProgram.setUniform("material", mesh.getMaterial());
                Texture text = mesh.getMaterial().getTexture();
                if (text != null) {
                    sceneShaderProgram.setUniform("numCols", text.getNumCols());
                    sceneShaderProgram.setUniform("numRows", text.getNumRows());
                }
            }

            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                        sceneShaderProgram.setUniform("selected", gameItem.isSelected() ? 1.0f : 0.0f);
                        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix);
                        sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
                        Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(gameItem, lightViewMatrix);
                        sceneShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
                    }
            );
        }
//
//        List<GameItem> gameItems = scene.getGameItems();
//        for (GameItem gameItem : gameItems) {
//            Mesh mesh = gameItem.getMesh();
//            // Set model view matrix for this item
//            Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix);
//            sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
//            // Render the mesh for this game item
//            if(mesh.getMaterial() != null) {
//                sceneShaderProgram.setUniform("material", mesh.getMaterial());
//            }
//            mesh.render();




//        if(!scene.getTempMapMeshes().isEmpty()) {
//            mapMeshes.putAll(scene.getTempMapMeshes());
//        }
//
//        scene.resetTempMapMeshes();



//        Map<Mesh, List<GameItem>> boundMeshes = scene.getBoundingMap();
//        for (Mesh mesh : boundMeshes.keySet()) {
//            if(mesh.getMaterial() != null) {
//                sceneShaderProgram.setUniform("material", mesh.getMaterial());
//            }
//            glActiveTexture(GL_TEXTURE2);
//            glBindTexture(GL_TEXTURE_2D, shadowMap.getDepthMapTexture().getId());
//            mesh.renderList(boundMeshes.get(mesh), (GameItem gameItem) -> {
//                        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(gameItem, viewMatrix);
//                        sceneShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
//                        Matrix4f modelLightViewMatrix = transformation.buildModelLightViewMatrix(gameItem, lightViewMatrix);
//                        sceneShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
//                    }
//            );
//        }



        sceneShaderProgram.unbind();
    }

    private void renderLights(Matrix4f viewMatrix, SceneLight sceneLight) {

        sceneShaderProgram.setUniform("ambientLight", sceneLight.getAmbientLight());
        sceneShaderProgram.setUniform("specularPower", specularPower);

        // Process Point Lights
        PointLight[] pointLightList = sceneLight.getPointLightList();
        int numLights = pointLightList != null ? pointLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the point light object and transform its position to view coordinates
            PointLight currPointLight = new PointLight(pointLightList[i]);
            Vector3f lightPos = currPointLight.getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;
            sceneShaderProgram.setUniform("pointLights", currPointLight, i);
        }

        // Process Spot Ligths
        SpotLight[] spotLightList = sceneLight.getSpotLightList();
        numLights = spotLightList != null ? spotLightList.length : 0;
        for (int i = 0; i < numLights; i++) {
            // Get a copy of the spot light object and transform its position and cone direction to view coordinates
            SpotLight currSpotLight = new SpotLight(spotLightList[i]);
            Vector4f dir = new Vector4f(currSpotLight.getConeDirection(), 0);
            dir.mul(viewMatrix);
            currSpotLight.setConeDirection(new Vector3f(dir.x, dir.y, dir.z));

            Vector3f lightPos = currSpotLight.getPointLight().getPosition();
            Vector4f aux = new Vector4f(lightPos, 1);
            aux.mul(viewMatrix);
            lightPos.x = aux.x;
            lightPos.y = aux.y;
            lightPos.z = aux.z;

            sceneShaderProgram.setUniform("spotLights", currSpotLight, i);
        }

        // Get a copy of the directional light object and transform its position to view coordinates
        DirectionalLight currDirLight = new DirectionalLight(sceneLight.getDirectionalLight());
        Vector4f dir = new Vector4f(currDirLight.getDirection(), 0);
        dir.mul(viewMatrix);
        currDirLight.setDirection(new Vector3f(dir.x, dir.y, dir.z));
        sceneShaderProgram.setUniform("directionalLight", currDirLight);
    }


//    private void renderSkyBox(Window window, Camera camera, Scene scene) {
//        skyBoxShaderProgram.bind();
//
//        skyBoxShaderProgram.setUniform("texture_sampler", 0);
//
//        // Update projection Matrix
//        Matrix4f projectionMatrix = window.getProjectionMatrix();
//        skyBoxShaderProgram.setUniform("projectionMatrix", projectionMatrix);
//        SkyBox skyBox = scene.getSkyBox();
//        Matrix4f viewMatrix = transformation.getViewMatrix();
//        viewMatrix.m30(0);
//        viewMatrix.m31(0);
//        viewMatrix.m32(0);
//        Matrix4f modelViewMatrix = transformation.buildModelViewMatrix(skyBox, viewMatrix);
//        skyBoxShaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
//        skyBoxShaderProgram.setUniform("ambientLight", scene.getSceneLight().getAmbientLight());
//
//        scene.getSkyBox().getMesh().render();
//
//        skyBoxShaderProgram.unbind();
//    }

    private void renderDepthMap(Window window, Camera camera, Scene scene) {
        // Setup view port to match the texture size
        glBindFramebuffer(GL_FRAMEBUFFER, shadowMap.getDepthMapFBO());
        glViewport(0, 0, ShadowMap.SHADOW_MAP_WIDTH, ShadowMap.SHADOW_MAP_HEIGHT);
        glClear(GL_DEPTH_BUFFER_BIT);

        depthShaderProgram.bind();

        DirectionalLight light = scene.getSceneLight().getDirectionalLight();
        Vector3f lightDirection = light.getDirection();

        float lightAngleX = (float)Math.toDegrees(Math.acos(lightDirection.z));
        float lightAngleY = (float)Math.toDegrees(Math.asin(lightDirection.x));
        float lightAngleZ = 0;
        Matrix4f lightViewMatrix = transformation.updateLightViewMatrix(new Vector3f(lightDirection).mul(light.getShadowPosMult()), new Vector3f(lightAngleX, lightAngleY, lightAngleZ));
        DirectionalLight.OrthoCoords orthCoords = light.getOrthoCoords();
        Matrix4f orthoProjMatrix = transformation.updateOrthoProjectionMatrix(orthCoords.left, orthCoords.right, orthCoords.bottom, orthCoords.top, orthCoords.near, orthCoords.far);

        depthShaderProgram.setUniform("orthoProjectionMatrix", orthoProjMatrix);



        Map<Mesh, List<GameItem>> mapMeshes = scene.getGameMeshes();
        for (Mesh mesh : mapMeshes.keySet()) {
            mesh.renderList(mapMeshes.get(mesh), (GameItem gameItem) -> {
                        Matrix4f modelLightViewMatrix = transformation.buildModelViewMatrix(gameItem, lightViewMatrix);
                        depthShaderProgram.setUniform("modelLightViewMatrix", modelLightViewMatrix);
                    }
            );
        }

//        if(!scene.getTempMapMeshes().isEmpty()) {
//            mapMeshes.putAll(scene.getTempMapMeshes());
//        }


        // Unbind
        depthShaderProgram.unbind();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }



    public void cleanup() {
        if (sceneShaderProgram != null) {
            sceneShaderProgram.cleanup();
        }
    }
}
