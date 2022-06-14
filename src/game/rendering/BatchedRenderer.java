package game.rendering;

import game.rendering.math.Matrix;
import game.rendering.math.Vector;
import java.util.ArrayList;
import java.util.HashMap;

import static org.lwjgl.opengl.GL33.*;

public class BatchedRenderer {
    private static final int VertexLength = 7;
    private static int VerticesPerObject = 0;
    private static int IndicesPerObject = 0;
    private static final VAO vao = new VAO();
    private static final ArrayList<RenderObject> objects = new ArrayList<>();
    private static final HashMap<RenderObject, float[]> objectVBOs = new HashMap<>();
    private static float[] vertices = new float[]{};
    private static final ArrayList<Float> VBOdata = new ArrayList<>();
    private static int[] EBOdata = new int[]{};
    private static int[] indices = new int[]{};
    private static boolean objectsUpdated;
    private static ShaderProgram shader;

    public static void InitializeRectRenderer(){
        Initialize(new float[]{
                0.5f, 0.5f, 1.0f, 1.0f, 0, 0, 0,
                0.5f, -0.5f, 1.0f, 0.0f, 0, 0, 0,
                -0.5f, -0.5f, 0.0f, 0.0f, 0, 0, 0,
                -0.5f, 0.5f, 0.0f, 1.0f, 0, 0, 0
        }, new int[]{
                0, 1, 3,
                3, 2, 1
        });
    }

    public static void Initialize(float[] objectVertices, int[] objectIndices){
        objectsUpdated = false;
        vertices = objectVertices;
        indices = objectIndices;
        VerticesPerObject = vertices.length / VertexLength;
        IndicesPerObject = indices.length;
        shader = new ShaderProgram("Batch");
        shader.use();
        vao.use();
        vao.uploadVertexData(GL_DYNAMIC_DRAW);
        vao.addVertexArrayAttribute(0, 2, VertexLength, 0); //coordinates
        vao.addVertexArrayAttribute(1, 3, VertexLength, 2); //texture coordinates
        vao.addVertexArrayAttribute(2, 1, VertexLength, 5); //texture map
        vao.addVertexArrayAttribute(3, 1, VertexLength, 6); //UI
        vao.uploadIndexData(GL_DYNAMIC_DRAW);

        int err = glGetError();
        if(err != 0){
            System.err.println("FAILED TO CREATE VAO: " + err);
        }
    }

    public static void add(RenderObject object){
        if(object.isUI()){
            objects.add(object);
        }
        else{
            objects.add(0, object);
        }
        calculateVBOData(object);
        objectsUpdated = true;
    }


    private static void calculateVBOData(RenderObject object){
        float[] VBO = new float[VerticesPerObject * VertexLength];
        for(int v = 0; v < VerticesPerObject; v++) {
            float[] vertex = new float[VertexLength];
            for (int i = 0; i < VertexLength; i++) {
                vertex[i] = BatchedRenderer.vertices[v * VertexLength + i];
            }

            Vector position = object.getTransform().multiply(Vector.Vec2(vertex[0], vertex[1]));
            Vector textureCoords;
            float textureID;
            if(object.hasTexture()){
                textureCoords = object.getTextureAtlas().getMatrix(object.getTexture())
                        .multiply(Vector.Vec3(vertex[2], vertex[3], vertex[4]));
                textureID = object.getTextureAtlas().getID();
            }
            else{
                textureCoords = Vector.Vec3(object.getR(), object.getG(), object.getB());
                textureID = -1.0f;
            }
            float UIFlag = object.isUI() ? 1.0f : 0.0f;

            VBO[v * VertexLength] = position.getX();
            VBO[v * VertexLength + 1] = position.getY();
            VBO[v * VertexLength + 2] = textureCoords.getX();
            VBO[v * VertexLength + 3] = textureCoords.getY();
            VBO[v * VertexLength + 4] = textureCoords.getZ();
            VBO[v * VertexLength + 5] = textureID;
            VBO[v * VertexLength + 6] = UIFlag;
        }
        objectVBOs.put(object, VBO);
    }
    private static void updateVertexData(){
        boolean updated = false;
        float[] VBOdata = new float[objects.size() * VertexLength * VerticesPerObject];
        for(int i = 0; i < objects.size(); i++){
            RenderObject object = objects.get(i);
            updated = object.updated || updated;
            if(object.updated){
                object.generateMatrix();
                calculateVBOData(object);
                object.updated = false;
            }
            float[] objVBO = objectVBOs.get(object);
            for(int j = 0; j < objVBO.length; j++){
                VBOdata[VerticesPerObject * VertexLength * i + j] = objVBO[j];
            }
        }
        uploadVertexData(VBOdata);
    }

    public static void updateVertexDataOld(){
        boolean updated = false;
        for (RenderObject object : objects) {
            updated = object.updated || updated;
            if (object.updated) {
                object.generateMatrix();
            }
        }

        if(updated) {
            VBOdata.clear();
            for (RenderObject object : BatchedRenderer.objects) {

                ArrayList<float[]> vertices = new ArrayList<>();
                for (int i = 0; i < VerticesPerObject; i++) {
                    float[] vertexData = new float[VertexLength];
                    for (int j = 0; j < VertexLength; j++) {
                        vertexData[j] = BatchedRenderer.vertices[i * VertexLength + j];
                    }
                    vertices.add(vertexData);
                }

                ArrayList<Vector> transformVectors = new ArrayList<>();
                ArrayList<Vector> textureVectors = new ArrayList<>();

                for (float[] vertex : vertices) {
                    transformVectors.add(Vector.Vec2(vertex[0], vertex[1]));
                    textureVectors.add(Vector.Vec3(vertex[2], vertex[3], vertex[4]));
                }

                Matrix objectTransformMatrix = object.getTransform();
                transformVectors.replaceAll(objectTransformMatrix::multiply);

                if (object.hasTexture()) {
                    Matrix objectTextureTransformMatrix = object.getTextureAtlas().getMatrix(object.getTexture());
                    textureVectors.replaceAll(objectTextureTransformMatrix::multiply);
                }

                for (int i = 0; i < VerticesPerObject; i++) {
                    VBOdata.add(transformVectors.get(i).getX());
                    VBOdata.add(transformVectors.get(i).getY());
                    VBOdata.add(object.hasTexture() ? textureVectors.get(i).getX() : object.getR());
                    VBOdata.add(object.hasTexture() ? textureVectors.get(i).getY() : object.getG());
                    VBOdata.add(object.hasTexture() ? textureVectors.get(i).getZ() : object.getB());
                    VBOdata.add(object.hasTexture() ? (float) object.getTextureAtlas().getID() : -1.0f);
                    VBOdata.add(object.isUI() ? 1.0f : 0.0f);
                }

                object.updated = false;
            }

            float[] VBO = new float[VBOdata.size()];
            for(int i = 0; i < VBOdata.size(); i++){
                VBO[i] = VBOdata.get(i);
            }
            uploadVertexData(VBO);
        }
    }


    private static void updateIndexData(){
        if(objectsUpdated){
            int objectCount = BatchedRenderer.objects.size();
            EBOdata = new int[objectCount * IndicesPerObject];
            for(int i = 0; i < objectCount; i++){
                int offset = i * VerticesPerObject;
                for(int j = 0; j < IndicesPerObject; j++){
                    EBOdata[i * IndicesPerObject + j] = indices[j] + offset;
                }
            }
            objectsUpdated = false;
            uploadIndexData();
        }
    }

    private static void uploadIndexData(){
        vao.setIndexArrayData(EBOdata);
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
        updateVertexDataOld();
        updateIndexData();
        vao.drawIndexed();
    }

    public static int getShaderID(){
        return shader.getID();
    }

}
