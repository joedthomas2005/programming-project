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
    private final int[] viewLocations;
    private final int[] projectionLocations;
    private float x, y;
    private boolean updated;

    public OrthoCamera2D(float x, float y, float width, float height, int... shaderPrograms){
        this.view = Matrix.Translation(-x, -y, 0);
        this.projection = Matrix.Ortho(0, width, 0, height, -1, 1);
        this.viewLocations = new int[shaderPrograms.length];
        this.projectionLocations = new int[shaderPrograms.length];
        for(int i = 0; i < shaderPrograms.length; i++) {
            this.viewLocations[i] = glGetUniformLocation(shaderPrograms[i], "view");
            this.projectionLocations[i] = glGetUniformLocation(shaderPrograms[i], "projection");
        }
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
        this.view = Matrix.Translation(this.x, this.y, 0);
        this.updated = true;
    }

    
    /** 
     * Return the view matrix of the camera.
     * @return Matrix
     */
    public Matrix getView(){
        return this.view;
    }

    
    /** 
     * Return the projection matrix of the camera. This will not change. 
     * @return Matrix
     */
    public Matrix getProjection(){
        return this.projection;
    }

    /**
     * Send the view matrix of this camera to the shader if it has changed.
     * If the camera has not moved then nothing will be done.
     */
    public void uploadViewUniform(){
        if(updated){
            for(int shader : viewLocations) glUniformMatrix4fv(shader, true, view.toArray());
            updated = false;
        }
    }

    /**
     * Send the projection matrix of this camera to the shader.
     * You should only have to do this once. 
     */
    public void uploadProjectionUniform(){
        glGetError();
        for(int shader : projectionLocations) glUniformMatrix4fv(shader, true, projection.toArray());
    }
}
