package game.engine.rendering;
import game.engine.Logger;

import static org.lwjgl.opengl.GL33.*;


/**
 * A wrapper for an openGL Vertex Array Object.
 * Holds a handle to a buffer intended for use as a GL_ARRAY_BUFFER VBO and
 * a buffer intended for use as a GL_ELEMENT_ARRAY_BUFFER EBO.
 * Also holds the vertex attribute arrays for the VBO buffer.
 */
public class VAO {
    
    private int ID;
    private int VBO;
    private int EBO;

    private float[] vertices;
    private int[] indices;

    public VAO(float[] vertices, int[] indices){
        
        this.vertices = vertices;
        this.indices = indices;

        this.ID = 0;
        this.VBO = 0;
        this.EBO = 0;

    }

    public VAO(){
        this.vertices = new float[]{};
        this.indices = new int[]{};
        this.ID = 0;
        this.VBO = 0;
        this.EBO = 0;
    }

    public void generate(){
        this.ID = glGenVertexArrays();
        this.VBO = glGenBuffers();
        this.EBO = glGenBuffers();
    }
    
    /**
     * Begin operating on this VAO.
     */
    public void use(){
        glBindVertexArray(this.ID);
        int err = glGetError();
        if(err != 0){
            Logger.getInstance().error("FAILED TO BIND VERTEX ARRAY: " + err);
        }
    }

    public void setVertexArrayData(float[] data){
        this.vertices = data;
    }
    
    public void setIndexArrayData(int[] data){
        this.indices = data;
    }
    /**
     * Bind the VBO buffer to the GL_ARRAY_BUFFER target and send the stored vertex data.
     */
    public void uploadVertexData(int type){        
        glBindBuffer(GL_ARRAY_BUFFER, this.VBO);
        glBufferData(GL_ARRAY_BUFFER, this.vertices, type);
    }

    /**
     * Bind the EBO buffer to the GL_ELEMENT_ARRAY_BUFFER target and upload the stored index data.
     */
    public void uploadIndexData(int type){
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, this.EBO);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, this.indices, type);
    }

    /**
     * Give this VAO access to a specified attribute array and add an attribute on that array which points to 
     * a set of values on the VBO. This attribute will be sent per vertex to the shader program. 
     * @param index the attribute array to enable
     * @param size the number of values in the attribute
     * @param stride the number of values between the starts of the instances of the attribute
     * @param start the number of values to offset this attribute from the start of the buffer by
     */
    public void addVertexArrayAttribute(int index, int size, int stride, int start){
        glBindBuffer(GL_ARRAY_BUFFER, this.VBO);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, GL_FLOAT, false, stride * Float.BYTES, (long) start * Float.BYTES);
    }


    public void drawIndexed(){
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

}
