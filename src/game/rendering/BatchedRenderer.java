package game.rendering;

import game.GameObject;
import game.rendering.math.Matrix;
import game.rendering.math.Vector;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL33.*;

public class BatchedRenderer {
    private static final int VertexLength = 5;
    private static int VerticesPerObject = 0;
    private static int IndicesPerObject = 0;
    private static final VAO vao = new VAO();
    private static final ArrayList<GameObject> objects = new ArrayList<>();
    private static float[] vertices = new float[]{};
    private static final ArrayList<Float> VBOdata = new ArrayList<>();
    private static int[] EBOdata = new int[]{};
    private static int[] indices = new int[]{};
    private static boolean objectsUpdated;

    public static void InitializeRectRenderer(){
        Initialize(new float[]{
                0.5f, 0.5f, 1.0f, 1.0f, 0,
                0.5f, -0.5f, 1.0f, 0.0f, 0,
                -0.5f, -0.5f, 0.0f, 0.0f, 0,
                -0.5f, 0.5f, 0.0f, 1.0f, 0
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
        vao.use();
        vao.uploadVertexData(GL_DYNAMIC_DRAW);
        vao.addVertexArrayAttribute(0, 2, VertexLength, 0); //coordinates
        vao.addVertexArrayAttribute(1, 2, VertexLength, 2); //texture coordinates
        vao.addVertexArrayAttribute(2, 1, VertexLength, 4); //texture map
        vao.uploadIndexData(GL_DYNAMIC_DRAW);

        int err = glGetError();
        if(err != 0){
            System.err.println("FAILED TO CREATE VAO: " + err);
        }
    }

    public static void add(GameObject object){
        objects.add(object);
        objectsUpdated = true;
    }

    private static void updateVertexData(){
        boolean updated = false;
        for(GameObject object : BatchedRenderer.objects){
            updated = object.updated || updated;
            if(object.updated){
                object.generateMatrix();
            }
        }

        if(updated){
            BatchedRenderer.VBOdata.clear();
            for(GameObject object : BatchedRenderer.objects){

                ArrayList<float[]> vertices = new ArrayList<>();
                for(int i = 0; i < VerticesPerObject; i++){
                    float[] vertexData = new float[VertexLength];
                    for(int j = 0; j < VertexLength; j++){
                        vertexData[j] = BatchedRenderer.vertices[i * VertexLength + j];
                    }
                    vertices.add(vertexData);
                }

                ArrayList<Vector> transformVectors = new ArrayList<>();
                ArrayList<Vector> textureVectors = new ArrayList<>();

                for(float[] vertex : vertices) {
                    transformVectors.add(Vector.Vec2(vertex[0], vertex[1]));
                    textureVectors.add(Vector.Vec2(vertex[2], vertex[3]));
                }

                Matrix objectTransformMatrix = object.getTransform();
                Matrix objectTextureTransformMatrix = object.getTextureAtlas().getMatrix(object.getTexture());
                transformVectors.replaceAll(objectTransformMatrix::multiply);
                textureVectors.replaceAll(objectTextureTransformMatrix::multiply);

                for(int i = 0; i < VerticesPerObject; i++){
                    VBOdata.add(transformVectors.get(i).getX());
                    VBOdata.add(transformVectors.get(i).getY());
                    VBOdata.add(textureVectors.get(i).getX());
                    VBOdata.add(textureVectors.get(i).getY());
                    VBOdata.add((float)object.getTextureAtlas().getID());
                }

                object.updated = false;
            }
            uploadVertexData();
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

    private static void uploadVertexData(){
        float[] VBOArray = new float[VBOdata.size()];
        for(int i = 0; i < VBOdata.size(); i++){
            VBOArray[i] = VBOdata.get(i);
        }

        vao.setVertexArrayData(VBOArray);
        vao.uploadVertexData(GL_DYNAMIC_DRAW);
    }

    /**
     * Draw all objects created by this renderer.
     * @param rebindVAO whether to rebind the internal vertex attribute array. Set this to true if you are using multiple renderers.
     */
    public static void drawAll(boolean rebindVAO){
        if(rebindVAO){
            vao.use();
        }
        updateVertexData();
        updateIndexData();
        vao.drawIndexed();
    }

}
