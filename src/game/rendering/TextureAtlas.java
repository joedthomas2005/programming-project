package game.rendering;

import game.rendering.math.Matrix;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;

public class TextureAtlas {

    private final boolean preGenerated;
    private Matrix[] preGeneratedMatrices;
    private final String path;
    private final int columns;
    private final int rows;
    private final float columnWidth;
    private final float rowHeight;
    private int ID = 0;

    public TextureAtlas(String path, int numberOfColumns, int numberOfRows, boolean preGenerated){
        this.path = path;
        this.columns = numberOfColumns;
        this.rows = numberOfRows;
        this.columnWidth = 1.0f / columns;
        this.rowHeight = 1.0f / rows;
        this.preGenerated = preGenerated;
    }

    public void load(int shaderProgram){
        glGetError();
        int[] widthB = {0}, heightB = {0}, channelsB = {0};
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer data = stbi_load(this.path, widthB, heightB, channelsB, 4);
        assert data != null;
        data.flip();
        int width = widthB[0];
        int height = heightB[0];
        ID = glGenTextures();
        glActiveTexture(GL_TEXTURE0 + ID);
        glBindTexture(GL_TEXTURE_2D, ID);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

        glGenerateMipmap(GL_TEXTURE_2D);
        glUniform1i(glGetUniformLocation(shaderProgram, "aTexture["+(ID)+"]"), ID);
        
        int err = glGetError();
        if(err != 0){
            System.err.println("ERROR CREATING TEXTURE ATLAS: " + err);
        }

        if(preGenerated){
            this.preGeneratedMatrices = new Matrix[rows * columns];
            for(int i = 0; i < rows * columns; i++){
                preGeneratedMatrices[i] = calculateMatrix(i);
            }
        }
        else {
            this.preGeneratedMatrices = new Matrix[]{};
        }

    }

    public int texCount(){
        return this.rows * this.columns;
    }
    public Matrix calculateMatrix(int texture){
        
        int textureX = texture;
        int textureY = 0;
        while(textureX > columns - 1){
            textureX -= columns;
            textureY++;  
        }

        return Matrix.Translation(textureX * columnWidth, textureY * rowHeight, 0).scale(columnWidth, rowHeight, 1);
    }

    public Matrix getMatrix(int texture){
        if(preGenerated){
            return preGeneratedMatrices[texture];
        }
        else{
            return calculateMatrix(texture);
        }
    }

    public int getID(){
        return this.ID;
    }
}
