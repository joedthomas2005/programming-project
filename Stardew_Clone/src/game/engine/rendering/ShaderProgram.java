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
        this.create();
    }

    public ShaderProgram(String renderType){
        this.vertexPath = renderType + "VertShader.vert";
        this.fallbackVertexPath = renderType + "VertShaderFallback.vert";
        this.fragPath = renderType + "FragShader.frag";
        this.fallbackFragmentPath = renderType + "FragShaderFallback.frag";
        this.create();
    }

    public ShaderProgram(String vertexShaderPath, String fragmentShaderPath, String fallbackVertexPath, String fallbackFragmentPath){
        this.vertexPath = vertexShaderPath;
        this.fragPath = fragmentShaderPath;
        this.fallbackFragmentPath = fallbackFragmentPath;
        this.fallbackVertexPath = fallbackVertexPath;
        this.create();
    }
    /**
     * Create an openGL shader program.
     */
    private void create(){
        Logger.log("STARTED SHADER BINDING: " + glGetError());
        Logger.log("LOADING SHADER SOURCE");
        
        try{
            vertexSource = Files.readString(Path.of(vertexPath));
            fragSource = Files.readString(Path.of(fragPath));
        }
        catch(IOException exception){
            Logger.error("COULDN'T LOAD SHADER SOURCE.\nError is " + exception);
        }

        int vShader = glCreateShader(GL_VERTEX_SHADER);
        int fShader = glCreateShader(GL_FRAGMENT_SHADER);
        Logger.log("SHADERS CREATED: " + glGetError());

        glShaderSource(vShader, vertexSource);
        glShaderSource(fShader, fragSource);
        Logger.log("SHADER SOURCE CODE LOADED: " + glGetError());
        
        glCompileShader(vShader);
        if(glGetShaderi(vShader, GL_COMPILE_STATUS) != GL_TRUE){
            Logger.error("ERROR COMPILING VERTEX SHADER: " + glGetShaderInfoLog(vShader));
            Logger.error("TRYING TO LOAD FALLBACK VERTEX SHADER.");
            try{
                vertexSource = Files.readString(Path.of("res/" + fallbackVertexPath));
            } catch (IOException e) {
                Logger.error("COULDN'T LOAD SHADER SOURCE.\nError is " + e);
            }
            glShaderSource(vShader, vertexSource);
            glCompileShader(vShader);
            if(glGetShaderi(vShader, GL_COMPILE_STATUS) != GL_TRUE){
                Logger.error("COMPILING VERTEX SHADER FALLBACK FAILED: " + glGetShaderInfoLog(vShader));
            }
            else{
                Logger.error("FALLBACK VERTEX SHADER COMPILED SUCCESSFULLY");
            }
        }

        glCompileShader(fShader);
        if(glGetShaderi(fShader, GL_COMPILE_STATUS) != GL_TRUE){
            Logger.error("ERROR COMPILING FRAGMENT SHADER: " + glGetShaderInfoLog(fShader));
            Logger.error("TRYING TO LOAD FALLBACK FRAGMENT SHADER.");
            try{
                fragSource = Files.readString(Path.of("res/"+ fallbackFragmentPath));
            } catch (IOException e){
                Logger.error("COULDN'T LOAD SHADER SOURCE. \nError is " + e);
            }
            glShaderSource(fShader, fragSource);
            glCompileShader(fShader);
            if(glGetShaderi(fShader, GL_COMPILE_STATUS) != GL_TRUE){
                Logger.error("COMPILING FRAGMENT SHADER FALLBACK FAILED " + glGetShaderInfoLog(fShader));
            }
            else{
                Logger.error("FALLBACK FRAGMENT SHADER COMPILED SUCCESSFULLY.");
            }
        }
        Logger.log("SHADER SOURCE CODE COMPILED: " + glGetError());

        int program = glCreateProgram();
        Logger.log("SHADER PROGRAM CREATED: " + glGetError());

        glAttachShader(program, vShader);
        glAttachShader(program, fShader);
        Logger.log("SHADERS ATTACHED: " + glGetError());

        glLinkProgram(program);
        Logger.log("SHADER PROGRAM LINKED: " + glGetError());
        glDeleteShader(vShader);
        glDeleteShader(fShader);
        Logger.log("SHADERS DELETED: " + glGetError());

        this.programID = program;
    }

    /**
     * Enable this shader program in the openGL context. 
     */
    public void use(){
        glUseProgram(programID);
    }

    /**
     * @return this shader program's openGL ID.
     */
    public int getID(){
        return this.programID;
    }

}
