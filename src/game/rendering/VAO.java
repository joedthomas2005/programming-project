package game.rendering;
import static org.lwjgl.opengl.GL33.*;

import java.util.ArrayList;

/**
 * A wrapper for an openGL Vertex Array Object.
 * Holds a handle to a buffer intended for use as a GL_ARRAY_BUFFER VBO,
 * a buffer intended for use as a GL_ELEMENT_ARRAY_BUFFER EBO 
 * and an array of any number of buffers intended for use as GL_ARRAY_BUFFER for any instance data.
 * Also holds the vertex attribute arrays for the VBO buffer and the instance buffers.
 */
public class VAO {
    
    private final int ID;
    private final int VBO;
    private final int EBO;
    private final int[] InstanceBuffers;

    private float[] vertices;
    private int[] indices;
    
    private final ArrayList<float[]> instanceData = new ArrayList<>();
    
    public VAO(float[] vertices, int[] indices, int instanceArrayCount){
        
        this.vertices = vertices;
        this.indices = indices;

        this.ID = glGenVertexArrays();        
        this.VBO = glGenBuffers();
        this.EBO = glGenBuffers();

        this.InstanceBuffers = new int[instanceArrayCount];
        for(int i = 0; i < instanceArrayCount; i++){
            this.InstanceBuffers[i] = glGenBuffers();
            this.instanceData.add(i, new float[]{});
        }
    }
    
    /**
     * Begin operating on this VAO.
     */
    public void use(){
        glBindVertexArray(this.ID);
    }

    /**
     * Set the specified instance array to some data given as floats.
     * @param array the index of the array to set
     * @param data the data to set that array to
     */
    public void setInstanceArrayData(int array, float[] data){
        this.instanceData.set(array, data);
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
     * Bind all instance array buffers and send the data for each. 
     */
    public void uploadAllInstanceData(){
        
        for(int i = 0; i < InstanceBuffers.length; i++){
            glBindBuffer(GL_ARRAY_BUFFER, this.InstanceBuffers[i]);
            glBufferData(GL_ARRAY_BUFFER, this.instanceData.get(i), GL_DYNAMIC_DRAW);
        }
    }

    /**
     * Bind a specified instance array buffer to the GL_ARRAY_BUFFER target and upload 
     * its respective data.
     * @param array the index of the array to upload
     */
    public void uploadInstanceData(int array){
        glBindBuffer(GL_ARRAY_BUFFER, this.InstanceBuffers[array]);
        glBufferData(GL_ARRAY_BUFFER, this.instanceData.get(array), GL_DYNAMIC_DRAW);
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

    /**
     * Give this VAO access to a specified attribute array and add an attribute on that array which points to 
     * a set of values on a specified instance buffer. This attribute will be sent per instance to the shader program. 
     * @param buffer the instance buffer to add the attribute to
     * @param index the attribute array to enable
     * @param size the number of values in the attribute
     * @param stride the number of values between the starts of the instances of the attribute
     * @param start the number of values to offset this attribute from the start of the buffer by
     */
    public void addInstanceArrayAttribute(int buffer, int index, int size, int stride, int start){
        glBindBuffer(GL_ARRAY_BUFFER, this.InstanceBuffers[buffer]);
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, size, GL_FLOAT, false, stride * Float.BYTES, (long) start * Float.BYTES);
        glVertexAttribDivisor(index, 1);
    }

    /**
     * Send the data for this VAO to the GPU and draw it a specified number of times. The count should match up with the 
     * amount of sets of data in the instance buffers.  
     * @param count the number of instances to draw this VAO. 
     */
    public void drawInstanced(int count){
        glDrawElementsInstanced(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0, count);
    }

    public void drawIndexed(){
        glDrawElements(GL_TRIANGLES, indices.length, GL_UNSIGNED_INT, 0);
    }

}
