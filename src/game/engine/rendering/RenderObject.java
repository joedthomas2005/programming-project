package game.engine.rendering;
import game.engine.rendering.math.Matrix;
import game.engine.rendering.math.Vector;

/**
 * An abstract thing with spacial position, rotation and size and a texture from an atlas.
 * It contains a transform matrix.
 */
public class RenderObject {
    private final boolean hasTexture;
    private Matrix transform;
    private Vector frameTranslation;
    private int texture;
    private TextureAtlas textureAtlas;
    private float[] vbo;
    protected boolean updated;
    private float x;
    private float y;
    private float rot;
    private float width;
    private float height;
    private final float r;
    private final float g;
    private final float b;
    public RenderObject(float x, float y, float rot, float width, float height, int texture, TextureAtlas textureAtlas){
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.width = width;
        this.height = height;
        this.updated = true;
        this.frameTranslation = new Vector(0, 0);
        this.transform = Matrix.identity(4)
            .translate(x, y, 0)
            .rotate(0, 0, rot)
            .scale(width, height, 1);
        this.texture = texture;
        this.textureAtlas = textureAtlas;
        this.hasTexture = true;
        this.r = 0;
        this.g = 0;
        this.b = 0;
        BatchedRenderer.add(this);
    }

    public RenderObject(float x, float y, float rot, float width, float height, float r, float g, float b){
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.width = width;
        this.height = height;
        this.updated = true;
        this.frameTranslation = new Vector(0, 0);
        this.transform = Matrix.identity(4)
                .translate(x, y, 0)
                .rotate(0, 0, rot)
                .scale(width, height, 1);
        this.texture = 0;
        this.textureAtlas = null;
        this.hasTexture = false;
        this.r = r;
        this.g = g;
        this.b = b;
        BatchedRenderer.add(this);
    }
    //Overridden by UIObject class. Feels very bad and might be a nightmare later, but I would rather not
    //have to work out how to split the vertex generation per object.
    public boolean isUI(){ return false; }

    public float[] getVbo(){
        return this.vbo;
    }

    public void buildVbo(){
        this.vbo = new float[BatchedRenderer.verticesPerObject * BatchedRenderer.VERTEX_LENGTH];
        for(int v = 0; v < BatchedRenderer.verticesPerObject; v++) {
            float[] vertex = new float[BatchedRenderer.VERTEX_LENGTH];
            for (int i = 0; i < BatchedRenderer.VERTEX_LENGTH; i++) {
                vertex[i] = BatchedRenderer.vertices[v * BatchedRenderer.VERTEX_LENGTH + i];
            }

            Vector position = this.transform.multiply(Vector.vec2(vertex[0], vertex[1]));
            Vector textureCoords;
            float textureID;
            if(hasTexture()){
                textureCoords = this.textureAtlas.getMatrix(this.texture)
                        .multiply(Vector.vec3(vertex[2], vertex[3], vertex[4]));
                textureID = this.textureAtlas.getId();
            }
            else{
                textureCoords = Vector.vec3(this.r, this.g, this.b);
                textureID = -1.0f;
            }

            vbo[v * BatchedRenderer.VERTEX_LENGTH] = position.getX();
            vbo[v * BatchedRenderer.VERTEX_LENGTH + 1] = position.getY();
            vbo[v * BatchedRenderer.VERTEX_LENGTH + 3] = textureCoords.getY();
            vbo[v * BatchedRenderer.VERTEX_LENGTH + 2] = textureCoords.getX();
            vbo[v * BatchedRenderer.VERTEX_LENGTH + 4] = textureCoords.getZ();
            vbo[v * BatchedRenderer.VERTEX_LENGTH + 5] = textureID;
            vbo[v * BatchedRenderer.VERTEX_LENGTH + 6] = isUI() ? 1.0f : 0.0f;
        }
    }

    /**
     * Set the object's current texture index on its bound atlas.
     * @param texture the texture index for the atlas
     */
    public void setTexture(int texture){
        this.texture = texture;
        this.updated = true;
    }

    /**
     * @return the current texture atlas (sprite-sheet) which this object's texture is loaded from.
     */
    public TextureAtlas getTextureAtlas(){
        return this.textureAtlas;
    }

    public void setTextureAtlas(TextureAtlas atlas){
        this.textureAtlas = atlas;
        this.updated = true;
    }
    /** 
     * Update the object's position and set its update flag to true. Does not regenerate the transform matrix.
     * @param x the change in the x coordinate
     * @param y the change in the y coordinate
     */
    public void move(float x, float y){
        this.x += x;
        this.y += y;
        this.updated = true;
    }

    /**
     * Apply a vector translation to this object's position and set its update flag to true.
     * Does not regenerate the transform matrix.
     * @param movement the vector 2 translation to apply
     */
    public void move(Vector movement){
        this.x += movement.getX();
        this.y += movement.getY();
        this.updated = true;
    }
    /** 
     * Update the object's rotation and set its update flag to true. Does not regenerate the transform matrix.
     * @param rot the rotation in degrees to apply
     */
    public void rotate(float rot){
        this.rot -= rot;
        this.updated = true;
    }


    /**
     * Generate this object's internal transform matrix.
     */
    public void generateMatrix(){
        this.x += frameTranslation.getX();
        this.y += frameTranslation.getY();

        this.transform = Matrix.identity(4)
            .translate(x, y, 0)
            .rotate(0, 0, rot)
            .scale(width, height, 1);
        this.frameTranslation = Vector.vec2(0, 0);
    }

    /**
     * Set the object's width
     * @param width the new width
     */
    public void setWidth(float width){
        this.width = width;
    }

    /**
     * Set the object's height
     * @param height the new height
     */
    public void setHeight(float height){
        this.height = height;
    }

    /**
     * Set the object's X coordinate
     * @param x the new X position
     */
    public void setX(float x){
        this.x = x;
    }

    /**
     * Set the object's Y coordinate
     * @param y the new Y position
     */
    public void setY(float y){
        this.y = y;
    }

    /**
     * Set the object's rotation
     * @param rot the new rotation in degrees
     */
    public void setRot(float rot){
        this.rot = rot;
    }

    /**
     * @return the object's current y position (float)
     */
    public float getY(){
        return this.y;
    }

    /**
     * @return the object's current x position (float)
     */
    public float getX(){
        return this.x;
    }

    /**
     * @return the object's current height (float)
     */
    public float getHeight(){
        return this.height;
    }

    /**
     * @return the object's current width (float)
     */
    public float getWidth(){
        return this.width;
    }

    public boolean hasTexture() { return this.hasTexture; }
    public float getR() { return this.r; }
    public float getG() { return this.g; }
    public float getB() { return this.b; }
}
