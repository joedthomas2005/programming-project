package game.engine;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;

import game.engine.rendering.Renderer;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;

/**
 * There must be an instance of this class for there to be an openGL context. This creates a glfw and GLCapabilities context and also 
 * handles window operations such as swapping buffers, closing and resizing. 
 */
public class Window{

    private int width;
    private int height;
    private boolean fullscreen;
    private final int swapInterval;
    private final String title;
    private long windowHandle = 0L;
    private long monitorHandle;
    private final float r;
    private final float g;
    private final float b;

    public Window(int width, int height, String title, boolean fullscreen, int swapInterval, float r, float g, float b) {
    
        this.width = width;
        this.height = height;
        this.fullscreen = fullscreen;
        this.swapInterval = swapInterval;
        this.title = title;
        this.r = r;
        this.g = g;
        this.b = b;
        this.monitorHandle = 0;
        create();
    }

    /**
     * Create the glfw window and context and load GLCapabilities.
     */
    public void create() {
        glfwInit();

        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        if(fullscreen){
            this.monitorHandle = glfwGetPrimaryMonitor();
        }

        this.windowHandle = glfwCreateWindow(this.width, this.height, this.title, this.monitorHandle, 0);
        
        if (this.windowHandle == 0) {
            glfwTerminate();
            return;
        }
        
        glfwMakeContextCurrent(this.windowHandle);
        GL.createCapabilities();
        glfwSwapInterval(this.swapInterval);
        glfwSetFramebufferSizeCallback(this.windowHandle, GLFWFramebufferSizeCallback.create((window, newWidth, newHeight) -> {
            this.width = newWidth;
            this.height = newHeight;
            glViewport(0, 0, newWidth, newHeight);
        }));
        
        glClearColor(r, g, b, 1.0f);
        
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
    }

    /**
     * Swap the display buffers and poll any glfw events. 
     */
    public void update(){
        Renderer.draw();
        glfwSwapBuffers(this.windowHandle);
        Input.resetEvents();
        glfwPollEvents();
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
    }

    /**
     * Check the close flag for this glfw window.
     * @return boolean
     */
    public boolean shouldClose(){
        return glfwWindowShouldClose(this.windowHandle);
    }

    public void close(){
        glfwSetWindowShouldClose(this.windowHandle, true);
    }
    /**
     * Kill this glfw context and unloads openGL.
     */
    public void end(){
        glfwTerminate();
        GL.destroy();
    }
    //Getters
    public long getWindow(){
        return this.windowHandle;
    }

    public int getHeight(){
        return this.height;
    }

    public int getWidth(){
        return this.width;
    }

    public int getSwapInterval(){
        return this.swapInterval;
    }

    public String getTitle(){
        return this.title;
    }

    public long getMonitor(){
        return this.monitorHandle;
    }

    public void setFullscreen(boolean fullscreen){
        this.fullscreen = fullscreen;
    }
}
