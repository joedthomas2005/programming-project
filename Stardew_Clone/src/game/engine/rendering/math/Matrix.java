package game.engine.rendering.math;

import game.engine.Logger;

/**
 * An any-sized matrix stored in row major notation.
 * (Bear in mind that openGL uses column major so .transpose() must be called before sending the data to the render program.) 
 */
public class Matrix {
    private final float[][] data;
    private final int rows;
    private final int columns;
    public Matrix(float[]... values){
        this.data = values;
        this.rows = values.length;
        this.columns = values[0].length;
    }

    
    /** 
     * @return a String representation of this matrix
     */
    @Override
    public String toString(){
        StringBuilder string = new StringBuilder();
        for(float[] row : data){
            for(float column : row){
                string.append(column).append(" ");
            }
            string.append("\n");
        }
        return string.toString();
    }

    
    /** 
     * Adds this matrix (left) and another given matrix (right) and returns the resultant matrix.
     * @param other the right hand matrix to add
     * @return the resultant Matrix
     */
    public Matrix add(Matrix other){
        float[][] result = new float[rows][columns];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                result[i][j] = this.data[i][j] + other.data[i][j];
            }
        }
        return new Matrix(result);
    }

    
    /** 
     * Multiplies this matrix (left) and another given matrix (right) and returns the resultant matrix.
     * @param other the right hand matrix to multiply
     * @return the resultant Matrix 
     */
    public Matrix multiply(Matrix other){
        float[][] result = new float[rows][columns];
        for(int row = 0; row < rows; row++){
            for(int column = 0; column < columns; column++){
                result[row][column] = 0;
                for(int i = 0; i < columns; i++){
                    result[row][column] += data[row][i] * other.data[i][column];
                }
            }
        }
        return new Matrix(result);
    }
    public Vector multiply(Vector vector){
        if(vector.items.length != this.columns){
            Main.logger.error("INVALID MATRIX VECTOR MULTIPLICATION ATTEMPTED");
            return vector;
        }
        else{
            float[] items = new float[vector.items.length];
            for(int row = 0; row < this.rows; row++){
                for(int column = 0; column < this.columns; column++){
                    items[row] += this.data[row][column] * vector.items[column];
                }
            }
            return new Vector(items);
        }
    }
    
    /** 
     * Construct an NxN sized identity matrix
     * @param rows the number of rows for the identity matrix
     * @return Matrix
     */
    public static Matrix identity(int rows){

        float[][] result = new float[rows][rows];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < rows; j++){
                result[i][j] = 0;
                if(i == j){
                    result[i][j] = 1;
                }
            }
        }
        return new Matrix(result);
    }

    /** 
     * Construct a transform matrix for a given 3D translation.
     * @param x the x translation
     * @param y the y translation
     * @param z the z translation
     * @return Matrix
     */
    public static Matrix translation(float x, float y, float z){
        Matrix result = identity(4);
        result.data[0][3] = x;
        result.data[1][3] = y;
        result.data[2][3] = z;
        return result;
    }

    
    /** 
     * Construct a transform matrix for a given 3D scale.
     * @param x the x scale factor
     * @param y the y scale factor
     * @param z the z scale factor
     * @return Matrix
     */
    public static Matrix scaling(float x, float y, float z){
        Matrix result = identity(4);
        result.data[0][0] = x;
        result.data[1][1] = y;
        result.data[2][2] = z;
        return result;
    }

    
    /** 
     * Construct a transform matrix for a given 3D rotation.
     * @param pitch rotation around the x axis
     * @param yaw rotation around the y axis
     * @param roll rotation around the z axis
     * @return Matrix
     */
    public static Matrix rotation(float pitch, float yaw, float roll){
        
        float pitchCosine = (float) Math.cos(Math.toRadians(pitch));
        float pitchSine = (float) Math.sin(Math.toRadians(pitch));
        float yawCosine = (float) Math.cos(Math.toRadians(yaw));
        float yawSine = (float) Math.sin(Math.toRadians(yaw));
        float rollCosine = (float) Math.cos(Math.toRadians(roll));
        float rollSine = (float) Math.sin(Math.toRadians(roll));

        Matrix pitchMatrix = new Matrix(
            new float[]{1, 0, 0, 0},
            new float[]{0, pitchCosine, -pitchSine, 0},
            new float[]{0, pitchSine, pitchCosine, 0},
            new float[]{0, 0, 0, 1}
        );

        Matrix yawMatrix = new Matrix(
            new float[]{yawCosine, 0, yawSine, 0},
            new float[]{0, 1, 0, 0},
            new float[]{-yawSine, 0, yawCosine, 0},
            new float[]{0, 0, 0, 1}
        );

        Matrix rollMatrix = new Matrix(
            new float[]{rollCosine, -rollSine, 0, 0},
            new float[]{rollSine, rollCosine, 0, 0},
            new float[]{0, 0, 1, 0},
            new float[]{0, 0, 0, 1}
        );

        return pitchMatrix.multiply(yawMatrix).multiply(rollMatrix);
    }

    
    /** 
     * Construct an orthographic projection matrix for a given viewport
     * @param left the x coordinate to map to the left of the screen
     * @param right the x coordinate to map to the right of the screen
     * @param bottom the y coordinate to map to the bottom of the screen
     * @param top the y coordinate to map to the top of the screen
     * @param near the near clipping pane
     * @param far the far clipping pane
     * @return Matrix
     */
    public static Matrix orthographic(float left, float right, float bottom, float top, float near, float far){
        return Matrix.identity(4).translate(
            -((right + left)/(right - left)),
            -((top + bottom)/(top - bottom)),
            -((far + near)/(far - near))
        ).scale(
            2/(right - left),
            2/(top - bottom),
            -2/(far - near)
        );

    }
    
    /** 
     * Return a transposed column-major version of this matrix. 
     * @return Matrix
     */
    public Matrix transpose(){
        float[][] result = new float[columns][rows];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                result[j][i] = data[i][j];
            }
        }
        return new Matrix(result);
    }

    
    /** 
     * Return this matrix multiplied by a transform matrix with a given translation.
     * @param x the x translation
     * @param y the y translation
     * @param z the z translation
     * @return Matrix
     */
    public Matrix translate(float x, float y, float z){
        return this.multiply(Matrix.translation(x, y, z));
    }

    
    /** 
     * Return this matrix multiplied by a transform matrix with a given rotation.
     * @param pitch the rotation around the x-axis
     * @param yaw the rotation around the y-axis
     * @param roll the rotation around the z-axis
     * @return Matrix
     */
    public Matrix rotate(float pitch, float yaw, float roll){
        return this.multiply(Matrix.rotation(pitch, yaw, roll));
    }

    
    /** 
     * Return this matrix multiplied by a transform matrix with a given scale.
     * @param x the x scale factor
     * @param y the y scale factor
     * @param z the z scale factor
     * @return Matrix
     */
    public Matrix scale(float x, float y, float z){
        return this.multiply(Matrix.scaling(x, y, z));
    }

    
    /** 
     * Return this matrix in an float array read across from the top left. 
     * @return float[]
     */
    public float[] toArray(){
        float[] floats = new float[rows * columns];
        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                floats[i * columns + j] = this.data[i][j];
            }
        }
        return floats;
    }

}
