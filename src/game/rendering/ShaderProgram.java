package game.rendering;
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
        System.out.println("STARTED SHADER BINDING: " + glGetError());
        System.out.println("LOADING SHADER SOURCE");
        
        try{
            vertexSource = Files.readString(Path.of("res/" + vertexPath));
            fragSource = Files.readString(Path.of("res/" + fragPath));
        }
        catch(IOException exception){
            System.err.println("COULDN'T LOAD SHADER SOURCE.\nError is " + exception);
        }

        int vShader = glCreateShader(GL_VERTEX_SHADER);
        int fShader = glCreateShader(GL_FRAGMENT_SHADER);
        System.out.println("SHADERS CREATED: " + glGetError());

        glShaderSource(vShader, vertexSource);
        glShaderSource(fShader, fragSource);
        System.out.println("SHADER SOURCE CODE LOADED: " + glGetError());
        
        glCompileShader(vShader);
        if(glGetShaderi(vShader, GL_COMPILE_STATUS) != GL_TRUE){
            System.err.println("ERROR COMPILING VERTEX SHADER: " + glGetShaderInfoLog(vShader));
            System.err.println("TRYING TO LOAD FALLBACK VERTEX SHADER.");
            try{
                vertexSource = Files.readString(Path.of("res/" + fallbackVertexPath));
            } catch (IOException e) {
                System.err.println("COULDN'T LOAD SHADER SOURCE.\nError is " + e);
            }
            glShaderSource(vShader, vertexSource);
            glCompileShader(vShader);
            if(glGetShaderi(vShader, GL_COMPILE_STATUS) != GL_TRUE){
                System.err.println("COMPILING VERTEX SHADER FALLBACK FAILED: " + glGetShaderInfoLog(vShader));
            }
            else{
                System.err.println("FALLBACK VERTEX SHADER COMPILED SUCCESSFULLY");
            }
        }

        glCompileShader(fShader);
        if(glGetShaderi(fShader, GL_COMPILE_STATUS) != GL_TRUE){
            System.err.println("ERROR COMPILING FRAGMENT SHADER: " + glGetShaderInfoLog(fShader));
            System.err.println("TRYING TO LOAD FALLBACK FRAGMENT SHADER.");
            try{
                fragSource = Files.readString(Path.of("res/"+ fallbackFragmentPath));
            } catch (IOException e){
                System.err.println("COULDN'T LOAD SHADER SOURCE. \nError is " + e);
            }
            glShaderSource(fShader, fragSource);
            glCompileShader(fShader);
            if(glGetShaderi(fShader, GL_COMPILE_STATUS) != GL_TRUE){
                System.err.println("COMPILING FRAGMENT SHADER FALLBACK FAILED " + glGetShaderInfoLog(fShader));
            }
            else{
                System.err.println("FALLBACK FRAGMENT SHADER COMPILED SUCCESSFULLY.");
            }
        }
        System.out.println("SHADER SOURCE CODE COMPILED: " + glGetError());

        int program = glCreateProgram();
        System.out.println("SHADER PROGRAM CREATED: " + glGetError());

        glAttachShader(program, vShader);
        glAttachShader(program, fShader);
        System.out.println("SHADERS ATTACHED: " + glGetError());

        glLinkProgram(program);
        System.out.println("SHADER PROGRAM LINKED: " + glGetError());
        glDeleteShader(vShader);
        glDeleteShader(fShader);
        System.out.println("SHADERS DELETED: " + glGetError());

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
