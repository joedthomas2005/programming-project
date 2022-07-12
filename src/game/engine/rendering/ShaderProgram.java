package game.engine.rendering;
import game.engine.Logger;

import java.nio.file.Path;
import java.io.IOException;
import java.nio.file.Files;

import static org.lwjgl.opengl.GL33.*;

/**
 * A shader program consists of a vertex and fragment shader. This class creates an 
 * openGL shader program from a vertex and fragment shader path. 
 */
public class ShaderProgram {
    
    private final String vertexPath;
    private final String fragPath;
    private final String fallbackVertexPath;
    private String vertexSource;
    private String fragSource;
    private final String fallbackFragmentPath;

    private int programID = 0;

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath){
        this.vertexPath = vertexShaderPath;
        this.fragPath = fragmentShaderPath;
        this.fallbackVertexPath = vertexPath.substring(0, vertexPath.length() - 6).concat("Fallback.vert");
        this.fallbackFragmentPath = fragPath.substring(0, fragPath.length() - 6).concat("Fallback.frag");
    }

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath, String fallbackVertexPath, String fallbackFragmentPath){
        this.vertexPath = vertexShaderPath;
        this.fragPath = fragmentShaderPath;
        this.fallbackFragmentPath = fallbackFragmentPath;
        this.fallbackVertexPath = fallbackVertexPath;
    }
    /**
     * Create an openGL shader program.
     */
    public void create(){
        Logger logger = Logger.getInstance();
        logger.log("STARTED SHADER BINDING: " + glGetError());
        logger.log("LOADING SHADER SOURCE");
        
        try{
            vertexSource = Files.readString(Path.of(vertexPath));
            fragSource = Files.readString(Path.of(fragPath));
        }
        catch(IOException exception){
            logger.error("COULDN'T LOAD SHADER SOURCE.\nError is " + exception);
        }

        int vShader = glCreateShader(GL_VERTEX_SHADER);
        int fShader = glCreateShader(GL_FRAGMENT_SHADER);
        logger.log("SHADERS CREATED: " + glGetError());

        glShaderSource(vShader, vertexSource);
        glShaderSource(fShader, fragSource);
        logger.log("SHADER SOURCE CODE LOADED: " + glGetError());
        
        glCompileShader(vShader);
        if(glGetShaderi(vShader, GL_COMPILE_STATUS) != GL_TRUE){
            logger.error("ERROR COMPILING VERTEX SHADER: " + glGetShaderInfoLog(vShader));
            logger.error("TRYING TO LOAD FALLBACK VERTEX SHADER.");
            try{
                vertexSource = Files.readString(Path.of("res/" + fallbackVertexPath));
            } catch (IOException e) {
                logger.error("COULDN'T LOAD SHADER SOURCE.\nError is " + e);
            }
            glShaderSource(vShader, vertexSource);
            glCompileShader(vShader);
            if(glGetShaderi(vShader, GL_COMPILE_STATUS) != GL_TRUE){
                logger.error("COMPILING VERTEX SHADER FALLBACK FAILED: " + glGetShaderInfoLog(vShader));
            }
            else{
                logger.error("FALLBACK VERTEX SHADER COMPILED SUCCESSFULLY");
            }
        }

        glCompileShader(fShader);
        if(glGetShaderi(fShader, GL_COMPILE_STATUS) != GL_TRUE){
            logger.error("ERROR COMPILING FRAGMENT SHADER: " + glGetShaderInfoLog(fShader));
            logger.error("TRYING TO LOAD FALLBACK FRAGMENT SHADER.");
            try{
                fragSource = Files.readString(Path.of("res/"+ fallbackFragmentPath));
            } catch (IOException e){
                logger.error("COULDN'T LOAD SHADER SOURCE. \nError is " + e);
            }
            glShaderSource(fShader, fragSource);
            glCompileShader(fShader);
            if(glGetShaderi(fShader, GL_COMPILE_STATUS) != GL_TRUE){
                logger.error("COMPILING FRAGMENT SHADER FALLBACK FAILED " + glGetShaderInfoLog(fShader));
            }
            else{
                logger.error("FALLBACK FRAGMENT SHADER COMPILED SUCCESSFULLY.");
            }
        }
        logger.log("SHADER SOURCE CODE COMPILED: " + glGetError());

        int program = glCreateProgram();
        logger.log("SHADER PROGRAM CREATED: " + glGetError());

        glAttachShader(program, vShader);
        glAttachShader(program, fShader);
        logger.log("SHADERS ATTACHED: " + glGetError());

        glLinkProgram(program);
        logger.log("SHADER PROGRAM LINKED: " + glGetError());
        glDeleteShader(vShader);
        glDeleteShader(fShader);
        logger.log("SHADERS DELETED: " + glGetError());

        this.programID = program;
    }

    /**
     * Enable this shader program in the openGL context. 
     */
    public void use(){
        glGetError();
        glUseProgram(programID);
        int err = glGetError();
        if(err != 0){
            Logger.getInstance().error("FAILED TO BIND SHADER PROGRAM: " + err);
        }
    }

    /**
     * @return this shader program's openGL ID.
     */
    public int getID(){
        return this.programID;
    }

}
