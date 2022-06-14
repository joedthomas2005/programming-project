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
    private boolean updated;

    public OrthoCamera2D(float x, float y, float width, float height){
        this.view = Matrix.Translation(-x, -y, 0);
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
        this.view = Matrix.Translation(this.x, this.y, 0);
        this.updated = true;
    }


    /**
     * Send the view matrix of this camera to the shader if it has changed.
     * If the camera has not moved then nothing will be done.
     */
    private void uploadViewUniform(){
        glUniformMatrix4fv(viewLocation, true, view.toArray());
    }

    /**
     * Send the projection matrix of this camera to the shader.
     * You should only have to do this once. 
     */
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
}
