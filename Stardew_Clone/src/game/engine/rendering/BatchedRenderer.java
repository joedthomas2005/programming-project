package game.engine.rendering;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL33.*;

public class BatchedRenderer {
    public static final int VERTEX_LENGTH = 7;
    protected static int verticesPerObject = 0;
    protected static int indicesPerObject = 0;
    public static final int MAX_LIGHTS = 10;
    private static final VAO vao = new VAO();
    private static int viewUniform;
    private static int projectionUniform;
    private static final ArrayList<RenderObject> objects = new ArrayList<>();
    private static final ArrayList<RenderObject> visible = new ArrayList<>();
    private static final ArrayList<PointLight> lights = new ArrayList<>();
    private static PointLight[] visibleLights = new PointLight[MAX_LIGHTS];
    private static final int[] lightPositionUniforms = new int[MAX_LIGHTS];
    private static final int[] lightColorUniforms = new int[MAX_LIGHTS];
    private static final int[] lightIntensityUniforms = new int[MAX_LIGHTS];
    private static int ambientLightUniform;
    private static OrthographicCamera2D viewport;
    protected static float[] vertices = new float[]{};
    private static int[] eboData = new int[]{};
    private static int[] indices = new int[]{};
    private static boolean objectsUpdated;
    private static boolean lightsUpdated;
    private static ShaderProgram shader;

    private BatchedRenderer(){}

    public static void initializeRectRenderer(OrthographicCamera2D camera){
        initialize(new float[]{
                0.5f, 0.5f, 1.0f, 1.0f, 0, 0, 0,
                0.5f, -0.5f, 1.0f, 0.0f, 0, 0, 0,
                -0.5f, -0.5f, 0.0f, 0.0f, 0, 0, 0,
                -0.5f, 0.5f, 0.0f, 1.0f, 0, 0, 0
        }, new int[]{
                0, 1, 3,
                3, 2, 1
        }, camera);
    }

    public static void initialize(float[] objectVertices, int[] objectIndices, OrthographicCamera2D camera){
        objectsUpdated = false;
        vertices = objectVertices;
        indices = objectIndices;
        viewport = camera;
        verticesPerObject = vertices.length / VERTEX_LENGTH;
        indicesPerObject = indices.length;
        shader = new ShaderProgram("Stardew_Clone/res/BatchVertShader.vert",
                "Stardew_Clone/res/BatchFragShader.frag");
        shader.use();
        vao.use();
        vao.uploadVertexData(GL_DYNAMIC_DRAW);
        vao.addVertexArrayAttribute(0, 2, VERTEX_LENGTH, 0); //coordinates
        vao.addVertexArrayAttribute(1, 3, VERTEX_LENGTH, 2); //texture coordinates
        vao.addVertexArrayAttribute(2, 1, VERTEX_LENGTH, 5); //texture map
        vao.addVertexArrayAttribute(3, 1, VERTEX_LENGTH, 6); //UI
        vao.uploadIndexData(GL_DYNAMIC_DRAW);

        int err = glGetError();
        if(err != 0){
            System.err.println("FAILED TO CREATE VAO: " + err);
        }

        for(int i = 0; i < MAX_LIGHTS; i++){
            lightPositionUniforms[i] = glGetUniformLocation(getShaderID(), "uLights["+i+"].position");
            lightColorUniforms[i] = glGetUniformLocation(getShaderID(), "uLights[" + i + "].color");
            lightIntensityUniforms[i] = glGetUniformLocation(getShaderID(), "uLights[" + i + "].intensity");
        }

        ambientLightUniform = glGetUniformLocation(getShaderID(), "ambient");
        viewUniform = glGetUniformLocation(getShaderID(), "view");
        projectionUniform = glGetUniformLocation(getShaderID(), "projection");
        uploadProjectionUniform();
        setAmbientLight(1, 1, 1, 1);
    }

    private static void uploadViewUniform(){
        glUniformMatrix4fv(viewUniform, true, viewport.getView().toArray());
    }

    private static void uploadProjectionUniform(){
        glUniformMatrix4fv(projectionUniform, true, viewport.getProjection().toArray());
    }

    public static void setAmbientLight(float r, float g, float b, float i){
        glUniform4f(ambientLightUniform, r, g, b, i);
    }
    private static void uploadLights(){
        glGetError();
        for(int i = 0; i < MAX_LIGHTS; i++){
            if(visibleLights[i] != null){
                glUniform2fv(lightPositionUniforms[i], new float[]{visibleLights[i].getX(), visibleLights[i].getY()});
                if(glGetError() != 0){
                    System.err.println("FAILED TO BIND POSITION UNIFORM");
                }
                glUniform3fv(lightColorUniforms[i], new float[]{visibleLights[i].getR(), visibleLights[i].getG(), visibleLights[i].getB()});
                if(glGetError() != 0){
                    System.err.println("FAILED TO BIND COLOR UNIFORM");
                }
                glUniform1f(lightIntensityUniforms[i], visibleLights[i].getIntensity());
                if(glGetError() != 0){
                    System.err.println("FAILED TO BIND INTENSITY UNIFORM");
                }
            }
            else{
                glUniform2fv(lightPositionUniforms[i], new float[]{0, 0});
                glUniform3fv(lightColorUniforms[i], new float[]{0, 0, 0});
                glUniform1f(lightIntensityUniforms[i], 0);

            }
        }
    }
    public static void updateLights(){
        for(PointLight light : lights){
            lightsUpdated = light.getUpdated() || lightsUpdated;
            light.setUpdated(false);
        }
        if(viewport.getUpdated() || lightsUpdated){
            visibleLights = new PointLight[MAX_LIGHTS];
            int curIndex = 0;
            for(PointLight light : lights){
                if(viewport.isVisible(light)){
                    if(curIndex >= MAX_LIGHTS){
                        break;
                    }
                    visibleLights[curIndex++] = light;
                }
            }
            lightsUpdated = false;
            uploadLights();
        }
    }

    public static void add(PointLight light){
        lights.add(light);
        lightsUpdated = true;
    }
    public static void add(RenderObject object){
        if(object.isUI()){
            objects.add(object);
        }
        else{
            objects.add(0, object);
        }
        object.buildVbo();
        objectsUpdated = true;
    }

    public static void end() {
        viewport.setUpdated(false);
    }
    private static void updateVertexData(){
        boolean updated = false;
        if(viewport.getUpdated()){
            viewport.updateViewMatrix();
            uploadViewUniform();
            visible.clear();
            for(RenderObject object : objects){
                if(viewport.isVisible(object) || object.isUI()){
                    visible.add(object);
                }
            }
        }
        float[] vboData = new float[verticesPerObject * VERTEX_LENGTH * visible.size()];
        for(int i = 0; i < visible.size(); i++){
            RenderObject object = visible.get(i);
            updated = object.updated || updated;
            if(object.updated){
                object.generateMatrix();
                object.buildVbo();
                object.updated = false;
            }
            for(int j = 0; j < object.getVbo().length; j++){
                vboData[verticesPerObject * VERTEX_LENGTH * i + j] = object.getVbo()[j];
            }
        }
        if(updated) {
            uploadVertexData(vboData);
        }
    }


    private static void updateIndexData(){
        if(objectsUpdated){
            int objectCount = BatchedRenderer.objects.size();
            eboData = new int[objectCount * indicesPerObject];
            for(int i = 0; i < objectCount; i++){
                int offset = i * verticesPerObject;
                for(int j = 0; j < indicesPerObject; j++){
                    eboData[i * indicesPerObject + j] = indices[j] + offset;
                }
            }
            objectsUpdated = false;
            uploadIndexData();
        }
    }

    private static void uploadIndexData(){
        vao.setIndexArrayData(eboData);
        vao.uploadIndexData(GL_DYNAMIC_DRAW);
    }

    private static void uploadVertexData(float[] data){
        vao.setVertexArrayData(data);
        vao.uploadVertexData(GL_DYNAMIC_DRAW);
    }

    /**
     * Draw all objects created by this renderer.
     */
    public static void draw(){
        updateLights();
        updateVertexData();
        updateIndexData();
        vao.drawIndexed();
        end();
    }

    public static int getShaderID(){
        return shader.getID();
    }
    public static OrthographicCamera2D getViewport(){ return viewport; }
}
