package game.engine.rendering;

import game.engine.Logger;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.opengl.GL33.*;

public final class RectRenderer2D {
    /**
     * This namespace allows things to be drawn to the screen. It is basically a big wrapper for lots of OpenGL.
     */
    public final int vertexLength;
    public int verticesPerObject;
    public final int maxLights;
    private final VAO vao;
    private int viewUniform;
    private int projectionUniform;
    private final ArrayList<RenderObject> objects;
    private final FastLinkedList<RenderObject> visible;
    private final ArrayList<PointLight> lights;
    private PointLight[] visibleLights;
    private final int[] lightPositionUniforms;
    private final int[] lightColorUniforms;
    private final int[] lightIntensityUniforms;
    private int ambientLightUniform;
    private OrthographicCamera2D viewport;
    public final float[] vertices;
    private int[] eboData;
    private final int[] indices;
    private boolean objectsUpdated;
    private boolean lightsUpdated;
    private final ShaderProgram shader;
    private int lastNonUI;

    private static RectRenderer2D instance = null;
    private RectRenderer2D(){
        this.vertices = new float[]{
                0.5f, 0.5f, 1.0f, 1.0f, 0, 0, 0,
                0.5f, -0.5f, 1.0f, 0.0f, 0, 0, 0,
                -0.5f, -0.5f, 0.0f, 0.0f, 0, 0, 0,
                -0.5f, 0.5f, 0.0f, 1.0f, 0, 0, 0
        };
        this.indices = new int[]{
                0, 1, 3,
                3, 2, 1
        };
        this.vertexLength = 7;
        this.maxLights = 10;
        this.verticesPerObject = vertices.length/vertexLength;
        this.objectsUpdated = false;
        this.lastNonUI = 0;
        this.lightColorUniforms = new int[maxLights];
        this.lightIntensityUniforms = new int[maxLights];
        this.lightPositionUniforms = new int[maxLights];
        this.objects = new ArrayList<>();
        this.visible = new FastLinkedList<>();
        this.lights = new ArrayList<>();
        this.vao = new VAO();
        this.viewport = null;
        this.shader = new ShaderProgram("res/shaders/BatchVertShader.vert",
                "res/shaders/BatchFragShader.frag");
    }

    public static RectRenderer2D getInstance(){
        if(instance == null){
            Logger.getInstance().log("CREATING RENDERER");
            instance = new RectRenderer2D();
            Logger.getInstance().log("RENDERER CREATED");
        }
        return instance;
    }


    public void initialize(OrthographicCamera2D camera){
        viewport = camera;
        shader.create();
        shader.use();

        vao.generate();
        vao.use();
        vao.uploadVertexData(GL_DYNAMIC_DRAW);
        vao.addVertexArrayAttribute(0, 2, vertexLength, 0); //coordinates
        vao.addVertexArrayAttribute(1, 3, vertexLength, 2); //texture coordinates
        vao.addVertexArrayAttribute(2, 1, vertexLength, 5); //texture map
        vao.addVertexArrayAttribute(3, 1, vertexLength, 6); //UI
        vao.uploadIndexData(GL_DYNAMIC_DRAW);

        int err = glGetError();
        if(err != 0){
            Logger.getInstance().error("failed to create vao: " + err);
        }

        int shaderID = shader.getID();
        for(int i = 0; i < maxLights; i++){
            lightPositionUniforms[i] = glGetUniformLocation(shaderID, "uLights["+i+"].position");
            lightColorUniforms[i] = glGetUniformLocation(shaderID, "uLights[" + i + "].color");
            lightIntensityUniforms[i] = glGetUniformLocation(shaderID, "uLights[" + i + "].intensity");
        }

        ambientLightUniform = glGetUniformLocation(shaderID, "ambient");
        viewUniform = glGetUniformLocation(shaderID, "view");
        projectionUniform = glGetUniformLocation(shaderID, "projection");
        uploadProjectionUniform();
        setAmbientLight(1, 1, 1, 1);
    }

    private void uploadViewUniform(){
        glUniformMatrix4fv(viewUniform, true, viewport.getView().toArray());
    }

    private void uploadProjectionUniform(){
        glUniformMatrix4fv(projectionUniform, true, viewport.getProjection().toArray());
    }

    public void setAmbientLight(float r, float g, float b, float i){
        glUniform4f(ambientLightUniform, r, g, b, i);
    }
    private void uploadLights(){
        glGetError();
        for(int i = 0; i < maxLights; i++){
            if(visibleLights[i] != null){
                glUniform2fv(lightPositionUniforms[i], new float[]{visibleLights[i].getX(), visibleLights[i].getY()});
                if(glGetError() != 0){
                    Logger.getInstance().error("FAILED TO BIND POSITION UNIFORM");
                }
                glUniform3fv(lightColorUniforms[i], new float[]{visibleLights[i].getR(), visibleLights[i].getG(), visibleLights[i].getB()});
                if(glGetError() != 0){
                    Logger.getInstance().error("FAILED TO BIND COLOR UNIFORM");
                }
                glUniform1f(lightIntensityUniforms[i], visibleLights[i].getIntensity());
                if(glGetError() != 0){
                    Logger.getInstance().error("FAILED TO BIND INTENSITY UNIFORM");
                }
            }
            else{
                glUniform2fv(lightPositionUniforms[i], new float[]{0, 0});
                glUniform3fv(lightColorUniforms[i], new float[]{0, 0, 0});
                glUniform1f(lightIntensityUniforms[i], 0);

            }
        }
    }
    public void updateLights(){
        for(PointLight light : lights){
            lightsUpdated = light.getUpdated() || lightsUpdated;
            light.setUpdated(false);
        }
        if(viewport.getUpdated() || lightsUpdated){
            visibleLights = new PointLight[maxLights];
            int curIndex = 0;
            for(PointLight light : lights){
                if(viewport.isVisible(light)){
                    if(curIndex >= maxLights){
                        break;
                    }
                    visibleLights[curIndex++] = light;
                }
            }
            lightsUpdated = false;
            uploadLights();
        }
    }

    public void add(PointLight light){
        lights.add(light);
        lightsUpdated = true;
    }
    public void add(RenderObject object){
        if(object.isUI()){
            objects.add(object);
        }
        else{
            objects.add(lastNonUI++, object);
        }
        object.buildVbo(this);
        objectsUpdated = true;
    }

    public void end() {
        viewport.setUpdated(false);
    }
    private void updateVertexData(){
        AtomicBoolean updated = new AtomicBoolean(false);
        if(viewport.getUpdated()){
            viewport.updateViewMatrix();
            uploadViewUniform();
            visible.clear();
            for(RenderObject object : objects){
                if(viewport.isVisible(object) || object.isUI()) {
                    visible.add(object);
                }
            }
        }
        float[] vboData = new float[vertices.length * visible.size()];
        AtomicInteger curObject = new AtomicInteger();
        visible.forEach((RenderObject object) -> {
            updated.set(object.updated || updated.get());
            if(object.updated){
                object.generateMatrix();
                object.buildVbo(this);
                object.updated = false;
            }
            for(int j = 0; j < object.getVbo().length; j++){
                vboData[vertices.length * curObject.get() + j] = object.getVbo()[j];
            }
            curObject.getAndIncrement();
        });

        if(updated.get()) {
            uploadVertexData(vboData);
        }
    }


    private void updateIndexData(){
        if(objectsUpdated){
            int objectCount = objects.size();
            eboData = new int[objectCount * indices.length];
            for(int i = 0; i < objectCount; i++){
                int offset = i * verticesPerObject;
                for(int j = 0; j < indices.length; j++){
                    eboData[i * indices.length + j] = indices[j] + offset;
                }
            }
            objectsUpdated = false;
            uploadIndexData();
        }
    }

    private void uploadIndexData(){
        vao.setIndexArrayData(eboData);
        vao.uploadIndexData(GL_DYNAMIC_DRAW);
    }

    private void uploadVertexData(float[] data){
        vao.setVertexArrayData(data);
        vao.uploadVertexData(GL_DYNAMIC_DRAW);
    }

    /**
     * Draw all objects created by this renderer.
     */
    public void draw(){
        updateLights();
        updateVertexData();
        updateIndexData();
        vao.drawIndexed();
        end();
    }

    public int getShaderID(){
        return shader.getID();
    }
}
