package game.rendering;
import game.rendering.math.Matrix;

import static org.lwjgl.opengl.GL33.*;

/**
 * An orthographic 2D camera which maps points in world space directly to screen space. 
 * No transformation is done based on depth or perspective so this is ideal for 
 * 2D rendering. An openGL shader program ID must be passed to the constructor as the 
 * view and perspective matrices are controlled by this and therefore the camera must know
 * the program where the uniforms are stored.
 */
public class OrthoCamera2D {
    private Matrix view;
    private final Matrix projection;
    private final int viewLocation;
    private final int projectionLocation;
    private float x, y;
    private final float width, height;
    private boolean updated;

    public OrthoCamera2D(float x, float y, float width, float height){
        this.x = -x;
        this.y = -y;
        this.width = width;
        this.height = height;
        this.view = Matrix.Translation(this.x, this.y, 0);
        this.projection = Matrix.Ortho(0, width, 0, height, -1, 1);
        this.viewLocation = glGetUniformLocation(BatchedRenderer.getShaderID(), "view");
        this.projectionLocation = glGetUniformLocation(BatchedRenderer.getShaderID(), "projection");
        this.updated = true;
        use();
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

    private void uploadViewUniform(){
        this.view = Matrix.Translation(this.x, this.y, 0);
        glUniformMatrix4fv(viewLocation, true, view.toArray());
    }

    private void uploadProjectionUniform(){
        glUniformMatrix4fv(projectionLocation, true, projection.toArray());
    }

    public void use(){
        uploadProjectionUniform();
        uploadViewUniform();
    }

    public void update(){
        if(updated){
            uploadViewUniform();
            updated = false;
        }
    }

    public float getWidth(){
        return width;
    }
    public float getHeight(){
        return height;
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }
}
