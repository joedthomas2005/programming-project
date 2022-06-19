package game.engine.rendering;
import game.engine.rendering.math.Matrix;

/**
 * An orthographic 2D camera which maps points in world space directly to screen space. 
 * No transformation is done based on depth or perspective so this is ideal for 
 * 2D rendering.
 */
public class OrthographicCamera2D {
    private Matrix view;
    private final Matrix projection;
    private float x;
    private float y;
    private final float width;
    private final float height;
    private boolean updated;

    public OrthographicCamera2D(float x, float y, float width, float height){
        this.x = -x;
        this.y = -y;
        this.width = width;
        this.height = height;
        this.view = Matrix.translation(this.x, this.y, 0);
        this.projection = Matrix.orthographic(0, width, 0, height, -1, 1);
        this.updated = true;
    }

    
    /** 
     * Move the camera. (translate everything else by the inverse)
     * @param x the amount to translate in the x direction
     * @param y the amount to translate in the y direction
     */

    public void move(float x, float y){
        this.x -= x;
        this.y -= y;
        this.updated = true;
    }

    public void setX(float x){
        this.x = -x;
        this.updated = true;
    }

    public void setY(float y){
        this.y = -y;
        this.updated = true;
    }

    public boolean getUpdated(){
        return updated;
    }

    public void setUpdated(boolean updated){
        this.updated = updated;
    }
    public void updateViewMatrix(){
        this.view = Matrix.translation(this.x, this.y, 0);
    }
    public Matrix getView(){ return view; }
    public Matrix getProjection(){ return projection; }
    public float getWidth(){
        return width;
    }
    public float getHeight(){
        return height;
    }

    public float getX(){
        return -x;
    }

    public float getY(){
        return -y;
    }
}
