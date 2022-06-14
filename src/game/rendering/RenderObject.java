package game.rendering;
import game.rendering.BatchedRenderer;
import game.rendering.TextureAtlas;
import game.rendering.math.Matrix;
import game.rendering.math.Vector;

/**
 * An abstract thing with spacial position, rotation and size and a texture from an atlas.
 * It contains a transform matrix.
 */
public class RenderObject {
    private static int objectCount = 0;
    private final int id;
    private final boolean hasTexture;
    private Matrix transform;
    private Vector frameTranslation;
    private int texture;
    private TextureAtlas textureAtlas;
    public boolean updated;
    private float x, y, rot, width, height;
    private final float r, g, b;
    public RenderObject(float x, float y, float rot, float width, float height, int texture, TextureAtlas textureAtlas){
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.width = width;
        this.height = height;
        this.updated = true;
        this.frameTranslation = new Vector(0, 0);
        this.transform = Matrix.Identity(4)
            .translate(x, y, 0)
            .rotate(0, 0, rot)
            .scale(width, height, 1);
        this.texture = texture;
        this.textureAtlas = textureAtlas;
        this.hasTexture = true;
        this.r = 0;
        this.g = 0;
        this.b = 0;
        this.id = objectCount;
        BatchedRenderer.add(this);
        objectCount++;
    }

    public RenderObject(float x, float y, float rot, float width, float height, float r, float g, float b){
        this.x = x;
        this.y = y;
        this.rot = rot;
        this.width = width;
        this.height = height;
        this.updated = true;
        this.frameTranslation = new Vector(0, 0);
        this.transform = Matrix.Identity(4)
                .translate(x, y, 0)
                .rotate(0, 0, rot)
                .scale(width, height, 1);
        this.texture = 0;
        this.textureAtlas = null;
        this.hasTexture = false;
        this.r = r;
        this.g = g;
        this.b = b;
        this.id = objectCount;
        BatchedRenderer.add(this);
        objectCount++;
    }
    //Overridden by UIObject class. Feels very bad and might be a nightmare later but I would rather not
    //have to work out how to split the vertex generation per object.
    public boolean isUI(){ return false; }


    /** 
     * @return this object's current transform matrix (an instance of Matrix)
     */
    public Matrix getTransform(){
        return this.transform;
    }
    
    /** 
     * @return this object's texture (an index on a texture sheet)
     */
    public int getTexture(){
        return this.texture;
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
     * @return the current texture atlas (spritesheet) which this object's texture is loaded from.
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

        this.transform = Matrix.Identity(4)
            .translate(x, y, 0)
            .rotate(0, 0, rot)
            .scale(width, height, 1);
        this.frameTranslation = Vector.Vec2(0, 0);
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
