package game.engine;
import static org.lwjgl.glfw.GLFW.*;
public final class InputController {
    private final boolean[] keysHeld = new boolean[GLFW_KEY_LAST];
    private final boolean[] keysFirstPressed = new boolean[GLFW_KEY_LAST];
    private final boolean[] keysReleased = new boolean[GLFW_KEY_LAST];
    private final boolean[] mouseButtons = new boolean[GLFW_MOUSE_BUTTON_LAST];
    private double mouseX = 0;
    private double mouseY = 0;
    private static InputController instance;
    private InputController(){}
    public static InputController getInstance(){
        if(instance == null){
            instance = new InputController();
        }
        return instance;
    }
    public void enable(Window window){

        glfwSetKeyCallback(window.getWindow(), (long windowHandle, int key, int scancode, int action, int mods) -> {
            if(action == GLFW_PRESS){
                keysHeld[key] = true;
                keysFirstPressed[key] = true;
            }
            else if(action == GLFW_RELEASE){
                keysHeld[key] = false;
                keysReleased[key] = true;
            }
        });

        glfwSetCursorPosCallback(window.getWindow(), (long windowHandle, double x, double y) -> {
            mouseX = x;
            mouseY = y;
        });

        glfwSetMouseButtonCallback(window.getWindow(), (long windowHandle, int button, int action, int mods) -> {
            if(action == GLFW_RELEASE){
                mouseButtons[button] = false;
            } else if (action == GLFW_PRESS) {
                mouseButtons[button] = true;
            }
        });
    }

    public void resetEvents(){
        for(int i = 0; i < GLFW_KEY_LAST; i++){
            keysFirstPressed[i] = false;
            keysReleased[i] = false;
        }
    }

    public boolean keyIsDown(int key){
        return keysHeld[key];
    }
    public boolean keyPressedDown(int key){
        return keysFirstPressed[key];
    }
    public boolean keyReleased(int key){
        return keysReleased[key];
    }

}
