package game.rendering;

import game.GameObject;
import game.rendering.math.Matrix;
import game.rendering.math.Vector;
import java.util.ArrayList;
import static org.lwjgl.opengl.GL33.*;

public class BatchedRenderer {
    private final int VertexLength = 5;
    private final int VerticesPerObject;
    private final int IndicesPerObject;
    private final VAO vao;
    private TextureAtlas textureSheet;
    private final ArrayList<GameObject> objects = new ArrayList<>();
    private final float[] vertices;
    private final ArrayList<Float> VBOdata = new ArrayList<>();
    private int[] EBOdata = new int[]{};
    private final int[] indices;
    private boolean objectsUpdated;

    public BatchedRenderer(float[] vertices, int[] indices){
        objectsUpdated = false;
        this.vertices = vertices;
        this.indices = indices;
        this.VerticesPerObject = vertices.length / VertexLength;
        this.IndicesPerObject = indices.length;
        this.vao = new VAO(new float[]{}, new int[]{}, 0);
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

    public static BatchedRenderer SquareRenderer(){
        return new BatchedRenderer(new float[]{
            0.5f, 0.5f, 1.0f, 1.0f, 0,
            0.5f, -0.5f, 1.0f, 0.0f, 0,
            -0.5f, -0.5f, 0.0f, 0.0f, 0,
            -0.5f, 0.5f, 0.0f, 1.0f, 0
        }, new int[]{
            0, 1, 3,
            3, 2, 1
        });
    }

    public GameObject create(float x, float y, float rot, float width, float height, int texture, TextureAtlas atlas){
        GameObject object = new GameObject(x, y, rot, width, height, texture, atlas);
        objects.add(object);
        objectsUpdated = true;
        return object;
    }

    private void updateVertexData(){
        boolean updated = false;
        for(GameObject object : objects){
            updated = object.updated || updated;
            if(object.updated){
                object.generateMatrix();
            }
        }

        if(updated){
            VBOdata.clear();
            for(GameObject object : objects){

                ArrayList<float[]> vertices = new ArrayList<>();
                for(int i = 0; i < VerticesPerObject; i++){
                    float[] vertexData = new float[VertexLength];
                    for(int j = 0; j < VertexLength; j++){
                        vertexData[j] = this.vertices[i * VertexLength + j];
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

    private void updateIndexData(){
        if(objectsUpdated){
            int objectCount = this.objects.size();
            this.EBOdata = new int[objectCount * IndicesPerObject];
            for(int i = 0; i < objectCount; i++){
                int offset = i * VerticesPerObject;
                for(int j = 0; j < IndicesPerObject; j++){
                    EBOdata[i * IndicesPerObject + j] = this.indices[j] + offset;
                }
            }
            objectsUpdated = false;
            uploadIndexData();
        }
    }

    private void uploadIndexData(){
        vao.setIndexArrayData(EBOdata);
        vao.uploadIndexData(GL_DYNAMIC_DRAW);
    }

    private void uploadVertexData(){
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
    public void drawAll(boolean rebindVAO){
        if(rebindVAO){
            vao.use();
        }
        updateVertexData();
        updateIndexData();
        vao.drawIndexed();
    }

}
